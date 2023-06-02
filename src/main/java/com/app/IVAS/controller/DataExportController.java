package com.app.IVAS.controller;

import com.app.IVAS.Enum.*;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.UploadDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.dto.data_transfer.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.security.PasswordService;
import com.app.IVAS.service.PlateNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/api/data-transfer")
public class DataExportController {

    private final RoleRepository roleRepository;
    private final PrefixRepository prefixRepository;
    private final PlateNumberService plateNumberService;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PortalUserRepository portalUserRepository;
    private final ZonalOfficeRepository zonalOfficeRepository;
    private final PasswordService passwordService;
    private final PlateNumberRepository plateNumberRepository;
    private final JwtService jwtService;
    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final ServiceTypeRepository serviceTypeRepository;


    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> transferUser(@RequestBody List<DataTransferUser> dtos) {
        for (DataTransferUser dto:dtos) {
            PortalUser portalUser = new PortalUser();
            portalUser.setCreatedAt(LocalDateTime.now());
            portalUser.setEmail(dto.getEmail());
            portalUser.setFirstName(dto.getName());
            portalUser.setLastName("");
            portalUser.setDisplayName(dto.getName());
            portalUser.setUsername(dto.getEmail());
            portalUser.setStatus(GenericStatusConstant.ACTIVE);
            portalUser.setPhoneNumber(dto.getPhone());
            portalUser.setUserVerified(false);
            portalUser.setAddress("AIRS office obiokoli street, Anambra");
            portalUser.setGeneratedPassword(passwordService.hashPassword("password"));
            portalUser.setRegType(RegType.REGISTRATION);

            if (dto.getStation() != null) {
                portalUser.setOffice(zonalOfficeRepository.findByName(dto.getStation()));
            }
            if (dto.getMla_id() != null) {
                portalUser.setRole(roleRepository.findByNameIgnoreCase("mla").orElseThrow(RuntimeException::new));
            } else {
                if (dto.getName().equalsIgnoreCase("chairman")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("chairman").orElseThrow(RuntimeException::new));
                } else if (dto.getName().equalsIgnoreCase("smr")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("smr").orElseThrow(RuntimeException::new));
                } else if  (dto.getName().equalsIgnoreCase("chairman2")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("chairman").orElseThrow(RuntimeException::new));
                } else if (dto.getName().equalsIgnoreCase("store")) {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("store").orElseThrow(RuntimeException::new));
                } else {
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("data processor").orElseThrow(RuntimeException::new));
                }
            }

            portalUserRepository.save(portalUser);
            log.info("user created ========= with email ================ " + portalUser.getEmail() );
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/stock")
    @Transactional
    public ResponseEntity<?> transferStock(@RequestBody List<DataTransferStock> dtos){
        for (DataTransferStock dto: dtos){
            PlateNumberDto stock = new PlateNumberDto();
            stock.setStartCode(prefixRepository.findByCode(dto.getStartletters()) != null ? prefixRepository.findByCode(dto.getStartletters()).getId() : null);
            stock.setEndCode(dto.getAlphabets());
            stock.setFirstNumber(dto.getFirst_number());
            stock.setLastNumber(dto.getLast_number());
            stock.setType(plateNumberTypeRepository.findByName(dto.getPlate_number_type().toUpperCase()).getId());

            if (dto.getFirst_number() != null && stock.getStartCode() != null){
                plateNumberService.createStock(stock);
                log.info("============ Stock created ================");
            } else if (stock.getStartCode() == null){
                log.info("============ invalid startCode ================" + dto.getStartletters());
            } else {
                log.info("============ invalid stock ================" + dto.getFirst_number() +  dto.getLast_number() + dto.getStartletters() + dto.getAlphabets() + dto.getPlate_number_type());
            }
        }

        return ResponseEntity.ok("");
    }

    @PostMapping("/plate-assignment")
    @Transactional
    public ResponseEntity<?> transferAssigment(@RequestBody List<DataTransferAssign> dtos){
        for (DataTransferAssign  dto:dtos){
            PortalUser mla = portalUserRepository.findFirstByUsernameIgnoreCase(dto.getMla());
            PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(dto.getPlatenumber());
           if (mla != null && plateNumber != null){
               plateNumber.setAgent(mla);
               plateNumber.setPlateNumberStatus(PlateNumberStatus.ASSIGNED);
               plateNumber.setLastUpdatedBy(jwtService.user);
               plateNumberRepository.save(plateNumber);
               log.info("============ plate number assigned ================" + dto.getPlatenumber());
           } else if (mla == null){
               log.info("============ invalid user ================" + dto.getMla());

           } else {
               log.info("============ invalid plate ================" + dto.getPlatenumber());
           }
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/sales")
    @Transactional
    public ResponseEntity<?> transferSales(@RequestBody List<DataTransferSales> dtos){
        for(DataTransferSales dto: dtos) {
            PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(dto.getPlate_number());
            if (plateNumber == null) {
                log.info("============ invalid plate ================" + dto.getPlate_number());
            } else {
                PortalUser owner = portalUserRepository.findFirstByUsernameIgnoreCase(dto.getPhonenumber());
                if (owner == null) {
                    PortalUser portalUser = new PortalUser();
                    portalUser.setCreatedAt(LocalDateTime.now());
                    portalUser.setEmail(dto.getPhonenumber());
                    portalUser.setFirstName(dto.getSoldto());
                    portalUser.setLastName("");
                    portalUser.setDisplayName(dto.getSoldto());
                    portalUser.setUsername(dto.getPhonenumber());
                    portalUser.setStatus(GenericStatusConstant.ACTIVE);
                    portalUser.setPhoneNumber(dto.getPhonenumber());
                    portalUser.setUserVerified(false);
                    portalUser.setAddress("AIRS office obiokoli street, Anambra");
                    portalUser.setGeneratedPassword(passwordService.hashPassword("password"));
                    portalUser.setRegType(RegType.REGISTRATION);
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("GENERAL_USER").orElseThrow(RuntimeException::new));
                    owner = portalUserRepository.save(portalUser);
                    log.info("============ taxpayer created ================" + dto.getSoldto());
                }
                plateNumber.setOwner(owner);
                plateNumber.setPlateNumberStatus(PlateNumberStatus.SOLD);
                plateNumber.setLastUpdatedBy(jwtService.user);
                plateNumberRepository.save(plateNumber);
                log.info("============ plate number sold ================" + dto.getPlate_number());

            }
        }

        return ResponseEntity.ok("");
    }

    @PostMapping("/vehicle")
    @Transactional
    public ResponseEntity<?> transferVehicleDetails(@RequestBody List<VehicleDetailsDto> dtos){
        for(VehicleDetailsDto dto: dtos) {
                PortalUser owner = portalUserRepository.findFirstByUsernameIgnoreCase(dto.getVehicle_owner_mobile_number());
                if (owner == null) {
                    PortalUser portalUser = new PortalUser();
                    portalUser.setCreatedAt(LocalDateTime.now());
                    portalUser.setEmail(dto.getVehicle_owner_mobile_number());
                    portalUser.setFirstName(dto.getVehicle_owner_full_name());
                    portalUser.setLastName("");
                    portalUser.setDisplayName(dto.getVehicle_owner_full_name());
                    portalUser.setUsername(dto.getVehicle_owner_mobile_number());
                    portalUser.setStatus(GenericStatusConstant.ACTIVE);
                    portalUser.setPhoneNumber(dto.getVehicle_owner_mobile_number());
                    portalUser.setUserVerified(false);
                    portalUser.setAddress("AIRS office obiokoli street, Anambra");
                    portalUser.setGeneratedPassword(passwordService.hashPassword("password"));
                    portalUser.setRegType(RegType.REGISTRATION);
                    portalUser.setRole(roleRepository.findByNameIgnoreCase("GENERAL_USER").orElseThrow(RuntimeException::new));
                    owner = portalUserRepository.save(portalUser);
                    log.info("============ taxpayer created ================" + owner.getDisplayName());
                }

                Vehicle vehicle = vehicleRepository.findFirstByChasisNumber(dto.getChassis_no());
                VehicleMake vehicleMake = vehicleMakeRepository.findFirstByNameIgnoreCase(dto.getMake());
                VehicleModel vehicleModel = vehicleModelRepository.findFirstByVehicleMakeAndNameIgnoreCase(vehicleMake, dto.getModel());
                PlateNumberType type = plateNumberTypeRepository.findByNameIgnoreCase(dto.getType());
                VehicleCategory category = vehicleCategoryRepository.findFirstByNameContains(dto.getCategory());
                PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(dto.getPlate());
                PlateNumber number = new PlateNumber();

                if (plateNumber == null){
                    number.setPlateNumber(dto.getPlate());
                    number.setType(type);
                    number.setOwner(owner);
                    number.setPlateNumberStatus(PlateNumberStatus.SOLD);
                    number.setPlateState(PlateState.OUT_OF_HOUSE);
                    number.setAgent(jwtService.user);
                    plateNumber = plateNumberRepository.save(number);
                    log.info("============ plate Number Created ================" + plateNumber.getPlateNumber());
                }

                if (vehicle == null){
                    Vehicle vehicle1 = new Vehicle();
                    vehicle1.setPortalUser(owner);
                    vehicle1.setColor(dto.getColour());
                    if (vehicleModel != null){
                        vehicle1.setVehicleModel(vehicleModel);
                    }

                    vehicle1.setChasisNumber(dto.getChassis_no());
                    vehicle1.setVehicleCategory(category);
                    vehicle1.setPlateNumber(plateNumber);
                    vehicle1.setCreatedBy(jwtService.user);
                    vehicle1.setYear(dto.getYear());
                    vehicle1.setCapacity(dto.getEngine_capacity());
                    Vehicle savedVehicle = vehicleRepository.save(vehicle1);
                    log.info("============ Vehicle Created ================" + savedVehicle.getChasisNumber());


//                    Invoice invoice = new Invoice();
//
//                    invoice.setAmount(dto.getAmount());
//                    invoice.setPaymentStatus(PaymentStatus.PAID);
//                    invoice.setPayer(owner);
//                    invoice.setVehicle(savedVehicle);
//                    invoice.setPaymentDate(LocalDateTime.from(dto.getDate()));
//                    invoice.setSentToTax(true);
//
//                    Invoice saved = invoiceRepository.save(invoice);
//                    log.info("============ Invoice Created ================" + saved.getAmount());
                }
            }
        return ResponseEntity.ok("");
    }


    @PostMapping("/invoice")
    @Transactional
    public ResponseEntity<?> transferInvoiceDetails(@RequestBody List<InvoiceDto> dtos){
        Double amount;
        for(InvoiceDto dto: dtos) {
            PortalUser owner = portalUserRepository.findFirstByUsernameIgnoreCase(dto.getVehicle_owner_mobile_number());
            Vehicle vehicle = vehicleRepository.findFirstByChasisNumber(dto.getChassis_no());
            PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(dto.getPlate());
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            Invoice invoice = new Invoice();

            List<InvoiceDto> invoiceDtos = new ArrayList<>();


            invoice.setAmount(dto.getAmount());
            invoice.setPaymentStatus(PaymentStatus.PAID);
            invoice.setPayer(owner);
            invoice.setVehicle(vehicle);
            invoice.setPaymentDate(dto.getTime_created());
            invoice.setSentToTax(true);

            Invoice saved = invoiceRepository.save(invoice);
            log.info("============ Invoice Created ================" + saved.getAmount());

        }
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/upload", consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<?> uplaodFromTax(@RequestBody List<UploadDto> dtos){

        System.out.println(dtos);
        for (UploadDto dto : dtos) {
            PortalUser payer = portalUserRepository.findFirstByFirstNameIgnoreCase(dto.getFirst_name());
            Vehicle vehicle = vehicleRepository.findFirstByPortalUser(payer);
            Invoice invoice = invoiceRepository.findFirstByVehicle(vehicle);
            ServiceType serviceType = serviceTypeRepository.findFirstByNameContains(dto.getDescription());

            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            invoiceServiceType.setInvoice(invoice);
            invoiceServiceType.setAmount(dto.getAmount());
            invoiceServiceType.setRegType(RegType.REGISTRATION);
            invoiceServiceType.setReference(dto.getReference_number());
            invoiceServiceType.setPaymentStatus(PaymentStatus.PAID);
            invoiceServiceType.setRevenuecode(dto.getItem_code());
//            invoiceServiceType.setPaymentDate(dto.getTime_created());
//            invoiceServiceType.setExpiryDate(dto.getTime_created().plusYears(1).minusDays(1));
            invoiceServiceType.setServiceType(serviceType);

            invoiceServiceTypeRepository.save(invoiceServiceType);

            System.out.println("Created Invoice");
        }
        return ResponseEntity.ok("");
    }
}
