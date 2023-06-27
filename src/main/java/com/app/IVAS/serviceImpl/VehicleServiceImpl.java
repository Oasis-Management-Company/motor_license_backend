package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.CardService;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import javax.sound.sampled.Port;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
    private final PaymentServiceImpl paymentService;
    private final CardService cardService;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleMakeRepository vehicleMakeRepository;
    private final CardRepository cardRepository;
    private final LegacyAssessmentRepo legacyAssessmentRepo;

    @Override
    public InvoiceDto getUserVehicleDetails(Long id) {
        InvoiceDto dto = new InvoiceDto();
        PortalUser user = portalUserRepository.findById(id).get();
        List<Vehicle> vehicles = vehicleRepository.findAllByPortalUserAndRegTypeIsNot(user, RegType.EDIT);
        List<Invoice> invoices = invoiceRepository.findByPayer(user);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        dto.setInvoices(invoices);
        dto.setVehicleDtos(vehicles);
        dto.setAddress(user.getAddress());
        dto.setEmail(user.getEmail());
        dto.setAsin(user.getAsin());
        dto.setFirstname(user.getDisplayName());
        dto.setPhonenumber(user.getPhoneNumber());
        dto.setPhoto(user.getImage());
        dto.setDateString(user.getCreatedAt().format(df));

        return dto;
    }

    @Override
    public VehicleDto getVehicleDetails(String chasis) {
        Vehicle vehicle = vehicleRepository.findByChasisNumberAndRegTypeIsNot(chasis, RegType.EDIT);
        List<Invoice> invoice = invoiceRepository.findByVehicle(vehicle);
        PortalUser user = portalUserRepository.findById(vehicle.getPortalUser().getId()).get();

        VehicleDto dto = new VehicleDto();
        dto.setInvoices(invoice);
        dto.setVehicle(vehicle);
        dto.setFirstname(user.getDisplayName());
//        dto.setLastname(user.getLastName());
        dto.setAddress(user.getAddress());
        dto.setPhonenumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setAsin(user.getAsin());
        return dto;
    }

    @Override
    public Vehicle saveEditedVehicle(VehicleDto vehicleDto) {
        Vehicle editVehicle = new Vehicle();
        Optional<Vehicle> oldEdit = Optional.ofNullable(vehicleRepository.findFirstByParentId(vehicleDto.getParent()));
        if(oldEdit.isPresent()){
            vehicleRepository.delete(oldEdit.get());
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleDto.getParent()).get();
        Optional<VehicleMake> vehicleMake = vehicleMakeRepository.findById(vehicleDto.getMakeId());
        Optional<VehicleModel> vehicleModel = vehicleModelRepository.findById(vehicleDto.getModelId());

        System.out.println(vehicleDto.getModelId());

        editVehicle.setVehicleModel(vehicleModel.get());
        editVehicle.setYear(vehicleDto.getYear());
        editVehicle.setColor(vehicleDto.getColor());
        editVehicle.setChasisNumber(vehicleDto.getChasis());
        editVehicle.setEngineNumber(vehicleDto.getEngine());
        editVehicle.setVehicleCategory(vehicleCategoryRepository.findById(Long.valueOf(vehicleDto.getCategory())).get());
        editVehicle.setLastUpdatedAt(LocalDateTime.now());
        editVehicle.setRegType(RegType.EDIT);
        editVehicle.setParentId(vehicle.getId());
        editVehicle.setCreatedBy(jwtService.user);
        editVehicle.setPortalUser(vehicle.getPortalUser());
        editVehicle.setPlateNumber(vehicle.getPlateNumber());

        if( ! vehicleDto.getPlate().equalsIgnoreCase(vehicle.getPlateNumber().getPlateNumber())){
            editVehicle.setPlateEdit(vehicleDto.getPlate());
        }else {
            editVehicle.setPlateEdit(vehicle.getPlateNumber().getPlateNumber());
        }

        vehicleRepository.save(editVehicle);

        return editVehicle;
    }

    public VehicleDto getVehicleDetailsByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findByPlateNumberAndRegTypeIsNot(plateNumber, RegType.EDIT);

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicle(vehicle);
        vehicleDto.setFirstname(vehicle.getPortalUser().getDisplayName());
        vehicleDto.setEmail(vehicle.getPortalUser().getEmail());
        vehicleDto.setAddress(vehicle.getPortalUser().getAddress());
        vehicleDto.setPhonenumber(vehicle.getPortalUser().getPhoneNumber());
        vehicleDto.setPlateType(vehicle.getPlateNumber().getType().getName());

        return vehicleDto;
    }

    @Override
    public List<ServiceType> getServiceTypeByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findByPlateNumberAndRegTypeIsNot(plateNumber, RegType.EDIT);

        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(vehicle.getVehicleCategory(),plateNumber.getType(), RegType.RENEWAL, RegType.COMPULSARY);
        return serviceTypes;
    }

    @Override
    public Invoice saveServiceTypeByPlate(String myplate, List<Long> ids) {

//        ids.add(479L);
//        ids.add(481L);
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(myplate);
        Vehicle vehicle = vehicleRepository.findByPlateNumberAndRegTypeIsNot(plateNumber, RegType.EDIT);
        Double totalAmount = 0.0;
        Sales sales1 = new Sales();

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }
        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();

        Invoice invoice = new Invoice();
        invoice.setVehicle(vehicle);
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setInvoiceNumber(invoiceNumber);
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
            invoiceServiceType.setReference(rrrGenerationService.generateNewRrrNumber());
            invoiceServiceType.setRegType(RegType.RENEWAL);
            invoiceServiceType.setAmount(serviceType.getPrice());
            invoiceServiceType.setPaymentStatus(PaymentStatus.NOT_PAID);
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        sales1.setVehicle(vehicle);
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.RENEWAL);
        salesRepository.save(sales1);

        cardService.createCard(savedInvoice, vehicle, RegType.RENEWAL);
        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
        }
        return savedInvoice;
    }

    @Override
    public InvoiceDto getTypeByInvoiceIdEdit(Long invoiceId) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy");
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
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setExpiry(invoice.getPaymentDate().plusMonths(1).format(df));
        dto.setDate(invoice.getCreatedAt());
        dto.setPlatenumber(invoice.getVehicle().getPlateNumber().getPlateNumber());
        dto.setDateString(invoice.getPaymentDate().format(df));
        dto.setExpiry(invoice.getPaymentDate().plusMonths(1).minusDays(1).format(df));

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

        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();
        Invoice invoice = new Invoice();
        invoice.setPayer(portalUser);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setAmount(totalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);



        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(rrrGenerationService.generateNewRrrNumber());
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
            System.out.println(e);
        }


        return savedInvoice;
    }

    @Override
    public AsinDto getTaxpayerByDetails(String phonenumber) {
        System.out.println(phonenumber);
        PortalUser portalUser = portalUserRepository.findFirstByPhoneNumber(phonenumber);
        AsinDto asinDto = new AsinDto();
        asinDto.setLastname(portalUser.getLastName());
        asinDto.setFirstname(portalUser.getDisplayName());
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
        List<InvoiceServiceType> invoiceServiceTypeArrayList = new ArrayList<>();

        Double totalAmount = 0.0;

        for (Long nid : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(nid).get();
            totalAmount += serviceType.getPrice();
        }

        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();
        Invoice invoice = new Invoice();
        invoice.setPayer(portalUser);
        invoice.setAmount(totalAmount);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (Long nid : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(nid).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setPaymentStatus(PaymentStatus.NOT_PAID);

            invoiceServiceType.setReference(rrrGenerationService.generateNewRrrNumber());
            if (serviceType.getRevenueCode() != null){
                invoiceServiceType.setRevenuecode(serviceType.getRevenueCode());
            }
            invoiceServiceType.setRegType(RegType.REGISTRATION);
            invoiceServiceType.setAmount(serviceType.getPrice());
            invoiceServiceTypeArrayList.add(invoiceServiceType);
        }
        invoiceServiceTypeRepository.saveAll(invoiceServiceTypeArrayList);

        Sales sales1 = new Sales();
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.NON_VEHICLE);
        salesRepository.save(sales1);

        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
            System.out.println(e);
        }

        return savedInvoice;
    }

    @Override
    public Invoice saveRenewalEdit(Long oldInvoiceId, Long customerId, List<Long> ids) {

        PortalUser portalUser = portalUserRepository.findById(customerId).get();
        List<InvoiceServiceType> invoiceServiceTypeArrayList = new ArrayList<>();

        Double totalAmount = 0.0;

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }

        Optional<Invoice> invoice = invoiceRepository.findById(oldInvoiceId);
        invoice.get().setAmount(totalAmount);
        invoice.get().setLastUpdatedBy(jwtService.user);
        invoice.get().setLastUpdatedAt(LocalDateTime.now());

        invoiceRepository.save(invoice.get());

        Optional<Invoice> deleteInvoice = invoiceRepository.findById(oldInvoiceId);

        Optional<List<InvoiceServiceType>> deleteInvoiceServiceTypes = invoiceServiceTypeRepository.findAllByInvoice(deleteInvoice.get());
        invoiceServiceTypeRepository.deleteAll(deleteInvoiceServiceTypes.get());


        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(invoice.get());
            invoiceServiceType.setPaymentStatus(PaymentStatus.NOT_PAID);

            invoiceServiceType.setReference(rrrGenerationService.generateNewRrrNumber());
            if (serviceType.getRevenueCode() != null){
                invoiceServiceType.setRevenuecode(serviceType.getRevenueCode());
            }
            invoiceServiceType.setRegType(RegType.RENEWAL);
            invoiceServiceType.setAmount(serviceType.getPrice());
            invoiceServiceTypeArrayList.add(invoiceServiceType);
        }
        invoiceServiceTypeRepository.saveAll(invoiceServiceTypeArrayList);

        return invoice.get();
    }


    @Override
    public List<SalesDto> searchTaxpayerAssessments(List<Sales> results) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
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
            dto.setInsuranceNumber(sales.getInvoice().getInvoiceNumber());
            dto.setMyDate(sales.getCreatedAt().format(df));
            dto.setId(sales.getId());
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<PortalUserPojo> searchTaxpayerAssessment(List<Sales> results) {
        return results.stream().map(user -> {
            PortalUserPojo dto = new PortalUserPojo();

            dto.setName(user.getInvoice().getPayer().getDisplayName());
            dto.setPhoneNumber(user.getInvoice().getPayer().getPhoneNumber());
            dto.setEmail(user.getInvoice().getPayer().getEmail());
            dto.setId(user.getInvoice().getPayer().getId());
            dto.setInvoiceNo(user.getInvoice().getInvoiceNumber());
            dto.setAmount(user.getInvoice().getAmount());
            LocalDateTime dateTime = LocalDateTime.parse(user.getCreatedAt().toString(), DateTimeFormatter.ISO_DATE_TIME);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            dto.setDateCreated(dateTime.format(outputFormatter));

            if(user.getInvoice().getPaymentStatus() != null) {
                dto.setPaymentStatus(user.getInvoice().getPaymentStatus());
            }

            dto.setInvoiceId(user.getInvoice().getId());

            return dto;

        }).collect(Collectors.toList());
    }


    @Override
    public List<InvoiceDto> searchAllInvoice(List<Invoice> invoices) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm");
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
            dto.setDateString(invoice.getCreatedAt().format(formatter));
            if(invoice.getPaymentDate() != null) {
                dto.setPaymentDateString(invoice.getPaymentDate().format(formatter));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> searchAllVehicleForApproval(List<Vehicle> results) {
        return results.stream().map(vehicle -> {
            VehicleDto dto = new VehicleDto();

            dto.setFirstname(vehicle.getPortalUser().getDisplayName());
            dto.setPhonenumber(vehicle.getPortalUser().getPhoneNumber());
            dto.setEmail(vehicle.getPortalUser().getEmail());
            dto.setDate(vehicle.getCreatedAt());
            dto.setCreatedBy(vehicle.getCreatedBy().getDisplayName());
            dto.setChasis(vehicle.getChasisNumber());
            dto.setParent(vehicle.getParentId());
            dto.setPlateType(vehicle.getPlateNumber().getType().getName());
            dto.setPlate(vehicle.getPlateNumber().getPlateNumber());
            dto.setColor(vehicle.getColor());
            dto.setEngine(vehicle.getEngineNumber());
            dto.setMake(vehicle.getVehicleModel().getVehicleMake().getName());
            dto.setModel(vehicle.getVehicleModel().getName());
            dto.setCategory(vehicle.getVehicleCategory().getName());
            dto.setYear(vehicle.getYear());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public VehicleEditDto getAllEditVehicle(Long id) {
        VehicleDto oldDetails = new VehicleDto();
        VehicleDto newDetails = new VehicleDto();
        VehicleEditDto vehicleEditDto = new VehicleEditDto();

        Vehicle old = vehicleRepository.findById(id).get();
        Vehicle news = vehicleRepository.findFirstByParentId(id);

        oldDetails.setFirstname(old.getPortalUser().getDisplayName());
        oldDetails.setPhonenumber(old.getPortalUser().getPhoneNumber());
        oldDetails.setEmail(old.getPortalUser().getEmail());
        oldDetails.setDate(old.getCreatedAt());
        oldDetails.setCreatedBy(old.getCreatedBy().getDisplayName());
        oldDetails.setChasis(old.getChasisNumber());
        oldDetails.setParent(old.getParentId());
        oldDetails.setPlateType(old.getPlateNumber().getType().getName());
        oldDetails.setPlate(old.getPlateNumber().getPlateNumber());
        oldDetails.setColor(old.getColor());
        oldDetails.setEngine(old.getEngineNumber());
        oldDetails.setMake(old.getVehicleModel().getVehicleMake().getName());
        oldDetails.setModel(old.getVehicleModel().getName());
        oldDetails.setCategory(old.getVehicleCategory().getName());
        oldDetails.setYear(old.getYear());


        newDetails.setFirstname(news.getPortalUser().getDisplayName());
        newDetails.setPhonenumber(news.getPortalUser().getPhoneNumber());
        newDetails.setEmail(news.getPortalUser().getEmail());
        newDetails.setDate(news.getCreatedAt());
        newDetails.setCreatedBy(news.getCreatedBy().getDisplayName());
        newDetails.setChasis(news.getChasisNumber());
        newDetails.setParent(news.getParentId());
        newDetails.setPlateType(news.getPlateNumber().getType().getName());
        newDetails.setPlate(news.getPlateEdit());
        newDetails.setColor(news.getColor());
        newDetails.setEngine(news.getEngineNumber());
        newDetails.setMake(news.getVehicleModel().getVehicleMake().getName());
        newDetails.setModel(news.getVehicleModel().getName());
        newDetails.setCategory(news.getVehicleCategory().getName());
        newDetails.setYear(news.getYear());

        vehicleEditDto.setOldDetails(oldDetails);
        vehicleEditDto.setNewDetails(newDetails);

        return vehicleEditDto;
    }

    @Override
    public HttpStatus approveEdittedVehicle(Long id, String type) {
        Vehicle old = vehicleRepository.findById(id).get();
        Vehicle news = vehicleRepository.findFirstByParentId(id);
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(old.getPlateNumber().getPlateNumber());

        Optional<List<Card>> cards = Optional.ofNullable(cardRepository.findAllByVehicleId(news.getId()));

        if(cards.isPresent()){
            for (Card card: cards.get()) {
                card.setVehicle(old);

                cardRepository.save(card);
            }
        }

        Optional<List<Invoice>> invoices = Optional.ofNullable(invoiceRepository.findAllByVehicleId(news.getId()));

        if (invoices.isPresent()){

            for (Invoice inv: invoices.get()) {

                inv.setVehicle(old);

                invoiceRepository.save(inv);

                Optional <List<InvoiceServiceType> >invoiceServiceTypes = invoiceServiceTypeRepository.findAllByInvoiceId(inv.getId());

                for (InvoiceServiceType insType: invoiceServiceTypes.get()) {
                    insType.setInvoice(inv);

                    invoiceServiceTypeRepository.save(insType);

                }
            }


        }


        if (type.equalsIgnoreCase("Approval")){
            old.setVehicleModel(news.getVehicleModel() == old.getVehicleModel() ? old.getVehicleModel() : news.getVehicleModel());
            old.setVehicleCategory(news.getVehicleCategory() == old.getVehicleCategory() ? old.getVehicleCategory() : news.getVehicleCategory());
            old.setColor(news.getColor() == old.getColor() ? old.getColor() : news.getColor());
            old.setChasisNumber(news.getChasisNumber()== old.getChasisNumber() ? old.getChasisNumber() : news.getChasisNumber());
            old.setEngineNumber(news.getEngineNumber()==old.getEngineNumber() ? old.getEngineNumber() : news.getEngineNumber());
            old.setYear(news.getYear()==old.getYear() ? old.getYear() : news.getYear());
            plateNumber.setPlateNumber(news.getPlateEdit());

            vehicleRepository.save(old);
            vehicleRepository.delete(news);
            return HttpStatus.OK;
        }else{

            vehicleRepository.delete(news);
            return HttpStatus.OK;
        }
    }

    @Override
    public List<ServiceType> getServiceTypeByPlateandType(String plate, String type) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findByPlateNumberAndRegTypeIsNot(plateNumber, RegType.EDIT);

        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(vehicle.getVehicleCategory(),plateNumber.getType(), RegType.valueOf(type.toUpperCase()), RegType.COMPULSARY);
        return serviceTypes;
    }

    @Override
    public Invoice saveServiceTypeByPlateLegacy(String myplate, List<Long> ids, String type, String paymentDate) {

        System.out.println(paymentDate.substring(0, 10));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime = LocalDate.parse(paymentDate.substring(0, 10), formatter);

        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(myplate);
        Vehicle vehicle = vehicleRepository.findByPlateNumberAndRegTypeIsNot(plateNumber, RegType.EDIT);
        Double totalAmount = 0.0;
        LegacyAssessment legacyAssessment = new LegacyAssessment();

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }
        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();

        Invoice invoice = new Invoice();
        invoice.setVehicle(vehicle);
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setAmount(totalAmount);
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaymentDate(dateTime.atStartOfDay());
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(rrrGenerationService.generateNewRrrNumber());
            invoiceServiceType.setRegType(RegType.LEGACY);
            invoiceServiceType.setAmount(serviceType.getPrice());
            invoiceServiceType.setPaymentStatus(PaymentStatus.PAID);
            invoiceServiceType.setPaymentDate(dateTime.atStartOfDay());

            if (serviceType.getName().contains("ROADWORTHINESS")) {
                if(plateNumber.getType().getName().equalsIgnoreCase("COMMERCIAL")){
                    invoiceServiceType.setExpiryDate(dateTime.atStartOfDay().plusMonths(6).minusDays(1));
                }else{
                    invoiceServiceType.setExpiryDate(dateTime.atStartOfDay().plusYears(1).minusDays(1));
                }
            }else{
                invoiceServiceType.setExpiryDate(dateTime.atStartOfDay().plusYears(1).minusDays(1));
            }


            invoiceServiceTypeRepository.save(invoiceServiceType);

        }

        legacyAssessment.setVehicle(vehicle);
        legacyAssessment.setInvoice(savedInvoice);
        legacyAssessment.setCreatedBy(jwtService.user);
        legacyAssessment.setPlateType(RegType.valueOf(type.toUpperCase()));
        legacyAssessmentRepo.save(legacyAssessment);

        return savedInvoice;
    }
}
