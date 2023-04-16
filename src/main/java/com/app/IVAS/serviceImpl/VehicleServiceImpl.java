package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.sound.sampled.Port;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final JwtService jwtService;
    private final SalesRepository salesRepository;
    private final RrrGenerationService rrrGenerationService;

    @Override
    public InvoiceDto getUserVehicleDetails(Long id) {
        InvoiceDto dto = new InvoiceDto();
        PortalUser user = portalUserRepository.findById(id).get();
        List<Vehicle> vehicles = vehicleRepository.findByPortalUser(user);
        List<Invoice> invoices = invoiceRepository.findByPayer(user);

        dto.setInvoices(invoices);
        dto.setVehicleDtos(vehicles);
        dto.setAddress(user.getAddress());
        dto.setEmail(user.getEmail());
        dto.setAsin(user.getAsin());
        dto.setFirstname(user.getDisplayName());
        dto.setPhonenumber(user.getPhoneNumber());
        dto.setPhoto(user.getImage());

        return dto;
    }

    @Override
    public VehicleDto getVehicleDetails(String chasis) {
        Vehicle vehicle = vehicleRepository.findFirstByChasisNumber(chasis);
        List<Invoice> invoice = invoiceRepository.findByVehicle(vehicle);
        PortalUser user = portalUserRepository.findById(vehicle.getPortalUser().getId()).get();

        VehicleDto dto = new VehicleDto();
        dto.setInvoices(invoice);
        dto.setVehicle(vehicle);
        dto.setFirstname(user.getFirstName());
        dto.setLastname(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPhonenumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Override
    public Vehicle saveEditedVehicle(VehicleDto vehicleDto) {

        Vehicle vehicle = vehicleRepository.findByChasisNumber(vehicleDto.getChasis());

        vehicle.setYear(vehicleDto.getYear());
        vehicle.setColor(vehicleDto.getColor());
        vehicle.setChasisNumber(vehicleDto.getChasis());
        vehicle.setEngineNumber(vehicleDto.getEngine());
        vehicle.setVehicleCategory(vehicleCategoryRepository.findById(Long.valueOf(vehicleDto.getCategory())).get());
        vehicle.setLastUpdatedAt(LocalDateTime.now());

        vehicleRepository.save(vehicle);

        return vehicle;
    }

    public VehicleDto getVehicleDetailsByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicle(vehicle);
        vehicleDto.setFirstname(vehicle.getPortalUser().getFirstName() + " " + vehicle.getPortalUser().getLastName());
        vehicleDto.setEmail(vehicle.getPortalUser().getEmail());
        vehicleDto.setAddress(vehicle.getPortalUser().getAddress());
        vehicleDto.setPhonenumber(vehicle.getPortalUser().getPhoneNumber());
        vehicleDto.setPlateType(vehicle.getPlateNumber().getType().getName());

        return vehicleDto;
    }

    @Override
    public List<ServiceType> getServiceTypeByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);

        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(vehicle.getVehicleCategory(),plateNumber.getType(), RegType.RENEWAL, RegType.COMPULSARY);
        return serviceTypes;
    }

    @Override
    public Invoice saveServiceTypeByPlate(String myplate, List<Long> ids) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(myplate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);
        Double totalAmount = 0.0;
        Sales sales1 = new Sales();

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }

        Invoice invoice = new Invoice();
        invoice.setVehicle(vehicle);
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setInvoiceNumber(rrrGenerationService.generateNewRrrNumber());
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setAmount(totalAmount);
        invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(rrrGenerationService.generateNewReferenceNumber());
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        sales1.setVehicle(vehicle);
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.RENEWAL);
        salesRepository.save(sales1);
        return savedInvoice;
    }

    @Override
    public InvoiceDto getTypeByInvoiceIdEdit(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        InvoiceDto dto = new InvoiceDto();
        dto.setPhonenumber(invoice.getPayer().getPhoneNumber());
        dto.setChasis(invoice.getVehicle().getChasisNumber());
        dto.setEngine(invoice.getVehicle().getEngineNumber());
        dto.setModel(invoice.getVehicle().getVehicleModel().getName());
        dto.setMake(invoice.getVehicle().getVehicleModel().getVehicleMake().getName());
        dto.setCategory(invoice.getVehicle().getVehicleCategory().getName());
        dto.setLastname(invoice.getPayer().getLastName());
        dto.setFirstname(invoice.getPayer().getFirstName());
        dto.setAddress(invoice.getPayer().getAddress());
        dto.setEmail(invoice.getPayer().getEmail());
        dto.setInvoiceNumber(invoice.getPaymentRef());
        dto.setDate(invoice.getCreatedAt());
        dto.setPlatenumber(invoice.getVehicle().getPlateNumber().getPlateNumber());


        return dto;
    }

    @Override
    public List<ServiceType> getTypeByInvoiceTaxpayer() {
        return serviceTypeRepository.findAllByCategoryIsNullAndPlateNumberTypeIsNull();
    }

    @Override
    public Invoice saveServiceTypeByPlateForTaxpayer(String phonenumber, List<Long> ids) {
        PortalUser portalUser = portalUserRepository.findFirstByPhoneNumber(phonenumber);

        Double totalAmount = 0.0;

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }

        Invoice invoice = new Invoice();
        invoice.setPayer(portalUser);
        invoice.setInvoiceNumber(rrrGenerationService.generateNewRrrNumber());
        invoice.setAmount(totalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);



        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(rrrGenerationService.generateNewReferenceNumber());
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }
        return savedInvoice;
    }

    @Override
    public AsinDto getTaxpayerByDetails(String phonenumber) {
        System.out.println(phonenumber);
        PortalUser portalUser = portalUserRepository.findFirstByPhoneNumber(phonenumber);
        AsinDto asinDto = new AsinDto();
        asinDto.setLastname(portalUser.getLastName());
        asinDto.setFirstname(portalUser.getFirstName());
        asinDto.setAddress(portalUser.getAddress());
        asinDto.setPhoneNumber(portalUser.getPhoneNumber());
        asinDto.setEmail(portalUser.getEmail());

        return asinDto;
    }

    @Override
    public List<ServiceType> getTaxpayerByDetailsServices() {
        return serviceTypeRepository.findAllByRegTypeOrRegType(RegType.NON_VEHICLE, RegType.COMPULSARY);
    }

    @Override
    public Invoice saveTaxPayerServiceType(Long id, List<Long> ids) {
        PortalUser portalUser = portalUserRepository.findById(id).get();

        Double totalAmount = 0.0;

        for (Long nid : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }

        Invoice invoice = new Invoice();
        invoice.setPayer(portalUser);
        invoice.setAmount(totalAmount);
        invoice.setInvoiceNumber(rrrGenerationService.generateNewRrrNumber());
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);



        for (Long nid : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(nid).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(rrrGenerationService.generateNewReferenceNumber());
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        Sales sales1 = new Sales();
//        sales1.setVehicle(vehicle);
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.NON_VEHICLE);
        salesRepository.save(sales1);

        return savedInvoice;
    }

    @Override
    public List<SalesDto> searchTaxpayerAssessments(List<Sales> results) {
        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();
            dto.setFirstname(sales.getInvoice().getPayer().getDisplayName());
            dto.setPhone_number(sales.getInvoice().getPayer().getPhoneNumber());
            dto.setAddress(sales.getInvoice().getPayer().getAddress());
            dto.setAsin(sales.getInvoice().getPayer().getAsin());
            dto.setEmail(sales.getInvoice().getPayer().getEmail());
            dto.setMla(sales.getCreatedBy().getDisplayName());
            dto.setDate(sales.getCreatedAt());
            dto.setAmount(sales.getInvoice().getAmount());
            dto.setStatus(sales.getInvoice().getPaymentStatus());
            dto.setApprovalStatus(sales.getApprovalStatus());
            dto.setId(sales.getId());
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<PortalUserPojo> searchTaxpayerAssessment(List<PortalUser> results) {
        return results.stream().map(user -> {
            PortalUserPojo dto = new PortalUserPojo();

            dto.setName(user.getDisplayName());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setEmail(user.getEmail());
            dto.setId(user.getId());
            LocalDateTime dateTime = LocalDateTime.parse(user.getCreatedAt().toString(), DateTimeFormatter.ISO_DATE_TIME);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            dto.setDateCreated(dateTime.format(outputFormatter));

            return dto;

        }).collect(Collectors.toList());
    }


    @Override
    public List<InvoiceDto> searchAllInvoice(List<Invoice> invoices) {
        return invoices.stream().map(invoice -> {
            InvoiceDto dto = new InvoiceDto();
            dto.setFirstname(invoice.getPayer().getDisplayName());
            dto.setPhonenumber(invoice.getPayer().getPhoneNumber());
            dto.setAsin(invoice.getPayer().getAsin());
            dto.setEmail(invoice.getPayer().getEmail());
            dto.setMla(invoice.getCreatedBy().getDisplayName());
            dto.setDate(invoice.getCreatedAt());
            dto.setAmount(invoice.getAmount());
            dto.setStatus(invoice.getPaymentStatus());
            dto.setInvoiceNumber(invoice.getInvoiceNumber());
            dto.setReference(invoice.getPaymentRef());
            dto.setId(invoice.getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
