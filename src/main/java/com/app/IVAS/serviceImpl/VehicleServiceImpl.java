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
import org.springframework.http.HttpStatus;
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
        Vehicle editVehicle = new Vehicle();
        Vehicle vehicle = vehicleRepository.findById(vehicleDto.getParent()).get();

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
        editVehicle.setVehicleModel(vehicle.getVehicleModel());

        vehicleRepository.save(editVehicle);

        return editVehicle;
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
            invoiceServiceType.setReference(invoiceNumber);
            invoiceServiceType.setRegType(RegType.RENEWAL);
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
            invoiceServiceType.setReference(invoiceNumber);
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
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceType.setReference(invoiceNumber);
            invoiceServiceType.setRegType(RegType.NON_VEHICLE);
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        Sales sales1 = new Sales();
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.NON_VEHICLE);
        salesRepository.save(sales1);

        return savedInvoice;
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
        newDetails.setPlate(news.getPlateNumber().getPlateNumber());
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

        if (type.equalsIgnoreCase("Approval")){
            old.setVehicleCategory(news.getVehicleCategory() == old.getVehicleCategory() ? old.getVehicleCategory() : news.getVehicleCategory());
            old.setColor(news.getColor() == old.getColor() ? old.getColor() : news.getColor());
            old.setChasisNumber(news.getChasisNumber()== old.getChasisNumber() ? old.getChasisNumber() : news.getChasisNumber());
            old.setEngineNumber(news.getEngineNumber()==old.getEngineNumber() ? old.getEngineNumber() : news.getEngineNumber());
            old.setYear(news.getYear()==old.getYear() ? old.getYear() : news.getYear());

            vehicleRepository.save(old);
            vehicleRepository.delete(news);
            return HttpStatus.OK;
        }else{
            vehicleRepository.delete(news);
            return HttpStatus.OK;
        }
    }
}
