package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.*;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.CardService;
import com.app.IVAS.service.SalesCtrlService;
import com.app.IVAS.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesCtrlServiceImpl implements SalesCtrlService {

    private final VehicleRepository vehicleRepository;
    private final UserManagementService userManagementService;
    private final SalesRepository salesRepository;
    private final InvoiceRepository invoiceRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    @Value("${asin_verification}")
    private String asinVerification;
    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final PortalUserRepository portalUserRepository;
    private final CardService cardService;
    private final InsuranceRepository insuranceRepository;



    @Override
    public Invoice SaveSales(SalesDto sales) {
        Vehicle vehicle = new Vehicle();
        Sales sales1 = new Sales();
        UserDto dto = new UserDto();
        Invoice invoice = new Invoice();
        SalesDto salesDto = new SalesDto();
        List<InvoiceServiceType> invoiceServiceTypeArrayList = new ArrayList<>();
        Double totalAmount = 0.0;


        PlateNumberType types = plateNumberTypeRepository.findById(sales.getPlatetype()).get();
        VehicleModel model = vehicleModelRepository.findById(sales.getModelId()).get();
        PlateNumber number = plateNumberRepository.findById(sales.getPlatenumber()).get();
        VehicleCategory category = vehicleCategoryRepository.findById(sales.getCategoryId()).get();
        Vehicle foundVehicle = vehicleRepository.findByChasisNumber(sales.getChasis());
        InsuranceCompany insuranceCompany = insuranceRepository.findById(sales.getInsurance()).get();
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegType(category, types, RegType.REGISTRATION);
        PortalUser portalUser = null;

        PortalUser user = portalUserRepository.findFirstByPhoneNumber(sales.getPhone_number());
        if (foundVehicle != null){
            return null;
        }

        if (user == null){
            dto.setAddress(sales.getAddress());
            dto.setEmail(sales.getEmail());
            dto.setFirstName(sales.getFirstname());
            dto.setLastName(sales.getLastname());
            dto.setPhoneNumber(sales.getPhone_number());
            dto.setPassword("password");
            dto.setRole("GENERAL_USER");
            dto.setAsin(sales.getAsin());

            Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
            portalUser = userManagementService.createUser(dto, jwtService.user, role);
        }else{
            portalUser = user;
        }

        vehicle.setPortalUser(portalUser);
        vehicle.setColor(sales.getColor());
        vehicle.setVehicleModel(model);
        vehicle.setChasisNumber(sales.getChasis());
        vehicle.setEngineNumber(sales.getEngine());
        vehicle.setVehicleCategory(category);
        vehicle.setPassengers(sales.getPassengers());
        vehicle.setPlateNumber(number);
        vehicle.setCreatedBy(jwtService.user);
        vehicle.setPolicySector(sales.getPolicy());
        vehicle.setYear(sales.getYear());
        vehicle.setInsurance(insuranceCompany);
        vehicle.setInsuranceNumber(sales.getInsuranceNumber());
        vehicle.setLoad(sales.getLoad());
        vehicle.setCapacity(sales.getCapacity());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        for (ServiceType type : serviceTypes) {
            totalAmount += type.getPrice();
        }

        invoice.setPayer(portalUser);
        invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoice.setInvoiceNumber("AIRS-" + LocalDate.now().getYear()+ (int)(Math.random()* 12345607));
        invoice.setAmount(totalAmount);
        invoice.setVehicle(savedVehicle);
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);


        for (ServiceType type : serviceTypes) {
            InvoiceServiceType serviceType = new InvoiceServiceType();
            serviceType.setServiceType(type);
            serviceType.setInvoice(savedInvoice);
            serviceType.setReference("IVS-" + LocalDate.now().getYear()+ (int)(Math.random()* 12345607));
            serviceType.setRevenuecode("2000787-7888900");

            invoiceServiceTypeArrayList.add(serviceType);
        }
        invoiceServiceTypeRepository.saveAll(invoiceServiceTypeArrayList);


        sales1.setVehicle(savedVehicle);
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.REGISTRATION);

        number.setOwner(portalUser);
        number.setPlateNumberStatus(PlateNumberStatus.SOLD);
        plateNumberRepository.save(number);
        Sales saved = salesRepository.save(sales1);
        cardService.createCard(savedInvoice, savedVehicle);


        return savedInvoice;
    }

    @Override
    public List<SalesDto> GetSales(List<Sales> results) {

        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();
            dto.setFirstname(sales.getVehicle().getPortalUser().getDisplayName());
            dto.setAddress(sales.getVehicle().getPortalUser().getAddress());
            dto.setAsin(sales.getVehicle().getPortalUser().getAsin());
            dto.setEmail(sales.getVehicle().getPortalUser().getEmail());
            dto.setChasis(sales.getVehicle().getChasisNumber());
            dto.setEngine(sales.getVehicle().getEngineNumber());
            dto.setColor(sales.getVehicle().getColor());
            dto.setModel(sales.getVehicle().getVehicleModel().getName());
            dto.setMake(sales.getVehicle().getVehicleModel().getVehicleMake().getName());
            dto.setCategory(sales.getVehicle().getVehicleCategory().getName());
            dto.setPlate(sales.getVehicle().getPlateNumber().getPlateNumber());
            dto.setMla(sales.getCreatedBy().getDisplayName());
            dto.setDate(sales.getCreatedAt());
            dto.setAmount(sales.getInvoice().getAmount());
            dto.setStatus(sales.getInvoice().getPaymentStatus());
            dto.setApprovalStatus(sales.getApprovalStatus());
            dto.setCategoryId(sales.getVehicle().getVehicleCategory().getId());
            dto.setId(sales.getId());
            return dto;

        }).collect(Collectors.toList());

    }

    @Override
    public AsinDto ValidateAsin(String asin) {

        AsinDto asinDto = new AsinDto();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<UserDemographyDto> responseRC = null;
        UserDemographyDto userinfo1 = null;

        String url = asinVerification + asin;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        try {
            responseRC = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,entity, UserDemographyDto.class);

            userinfo1 = responseRC.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert userinfo1 != null;
        asinDto.setAsin(userinfo1.getAsin());
        asinDto.setAddress(userinfo1.getAddress());
        asinDto.setEmail(userinfo1.getEmail());
        asinDto.setPhoneNumber(userinfo1.getPhoneNumber());
        asinDto.setName(userinfo1.getName());
        asinDto.setPhoto(userinfo1.getPhoto());
        asinDto.setFirstname(userinfo1.getFirstname());
        asinDto.setLastname(userinfo1.getLastname());
        return asinDto;
    }

    @Override
    public List<VehicleMake> getVehicleMake() {
        return vehicleMakeRepository.findAll();
    }

    @Override
    public List<VehicleModel> getVehicleModel(Long id) {
        VehicleMake make = vehicleMakeRepository.findById(id).get();
        return vehicleModelRepository.findAllByVehicleMake(make);
    }

    @Override
    public List<PlateNumber> getUserPlateNumbers(Long id) {
        PlateNumberType type = plateNumberTypeRepository.findById(id).get();
        List<PlateNumber> plateNumbers = plateNumberRepository.findAllByAgentAndPlateNumberStatusAndTypeAndOwnerIsNull(jwtService.user, PlateNumberStatus.ASSIGNED,type);
        return plateNumbers;
    }

    @Override
    public List<PlateNumberType> getUserPlateNumberTypes() {
        return plateNumberTypeRepository.findAll();
    }

    @Override
    public List<VehicleCategory> getVehicleCategory() {
        return vehicleCategoryRepository.findAll();
    }

    @Override
    public List<ServiceType> getServiceType(Long id) {
        VehicleCategory category = vehicleCategoryRepository.findById(id).get();
        return serviceTypeRepository.findAllByCategory(category);
    }

    @Override
    public String getApprovalStatus(Long id, String action,  Optional<String> reason) {
        Sales sales = salesRepository.findById(id).orElseThrow(RuntimeException::new);

        if (action.equalsIgnoreCase("APPROVED")){
            sales.setApprovalStatus(ApprovalStatus.APPROVED);
            sales.setApprovedDate(LocalDateTime.now());
            sales.setApprovedBy(jwtService.user);
        } else if (action.equalsIgnoreCase("QUERIED")){
            sales.setApprovalStatus(ApprovalStatus.QUERIED);
            sales.setApprovedDate(LocalDateTime.now());
            sales.setApprovedBy(jwtService.user);
            sales.setReason(reason.orElse("Not Stated"));
            sales.setApprovedBy(jwtService.user);
        }

        sales.setLastUpdatedAt(LocalDateTime.now());
        salesRepository.save(sales);
        return "Application was successfully" + " " + action;
    }

    @Override
    public List<SalesDto> searchAllForVIO(List<Sales> results) {
        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();
            dto.setFirstname(sales.getVehicle().getPortalUser().getDisplayName());
            dto.setAddress(sales.getVehicle().getPortalUser().getAddress());
            dto.setAsin(sales.getVehicle().getPortalUser().getAsin());
            dto.setEmail(sales.getVehicle().getPortalUser().getEmail());
            dto.setChasis(sales.getVehicle().getChasisNumber());
            dto.setEngine(sales.getVehicle().getEngineNumber());
            dto.setColor(sales.getVehicle().getColor());
            dto.setModel(sales.getVehicle().getVehicleModel().getName());
            dto.setMake(sales.getVehicle().getVehicleModel().getVehicleMake().getName());
            dto.setCategory(sales.getVehicle().getVehicleCategory().getName());
            dto.setPlate(sales.getVehicle().getPlateNumber().getPlateNumber());
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
    public List<VehicleDto> searchAllVehicles(List<Vehicle> results) {
        return results.stream().map(sales -> {
            VehicleDto dto = new VehicleDto();
            dto.setChasis(sales.getChasisNumber());
            dto.setEngine(sales.getEngineNumber());
            dto.setColor(sales.getColor());
            dto.setModel(sales.getVehicleModel().getName());
            dto.setMake(sales.getVehicleModel().getVehicleMake().getName());
            dto.setCategory(sales.getVehicleCategory().getName());
            dto.setPlate(sales.getPlateNumber().getPlateNumber());
            dto.setDate(sales.getCreatedAt());
            dto.setYear(sales.getYear());
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public SalesDto AddVehicle(SalesDto sales) {
        System.out.println(sales);
        Vehicle vehicle = new Vehicle();
        UserDto dto = new UserDto();
        PlateNumber number = new PlateNumber();
        PortalUser portalUser = null;

        VehicleModel model = vehicleModelRepository.findById(sales.getModelId()).get();
        VehicleCategory category = vehicleCategoryRepository.findById(sales.getCategoryId()).get();
        PlateNumberType type = plateNumberTypeRepository.findById(sales.getPlatetype()).get();
        InsuranceCompany insuranceCompany = insuranceRepository.findById(sales.getInsurance()).get();
        portalUser = portalUserRepository.findFirstByPhoneNumber(sales.getPhone_number());

        if (portalUser == null){
            dto.setAddress(sales.getAddress());
            dto.setEmail(sales.getEmail());
            dto.setFirstName(sales.getFirstname());
            dto.setLastName(sales.getLastname());
            dto.setPhoneNumber(sales.getPhone_number());
            dto.setPassword("password");
            dto.setRole("GENERAL_USER");

            Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
            portalUser = userManagementService.createUser(dto, jwtService.user, role);

        }


        number.setPlateNumber(sales.getPlate());
        number.setType(type);
        number.setOwner(portalUser);
        number.setPlateNumberStatus(PlateNumberStatus.SOLD);
        number.setPlateState(PlateState.OUT_OF_HOUSE);
        number.setAgent(jwtService.user);

        PlateNumber plateNumber = plateNumberRepository.save(number);

        vehicle.setPortalUser(portalUser);
        vehicle.setColor(sales.getColor());
        vehicle.setVehicleModel(model);
        vehicle.setChasisNumber(sales.getChasis());
        vehicle.setEngineNumber(sales.getEngine());
        vehicle.setVehicleCategory(category);
        vehicle.setPassengers(sales.getPassengers());
        vehicle.setPlateNumber(plateNumber);
        vehicle.setCreatedBy(jwtService.user);
        vehicle.setPolicySector(sales.getPolicy());
        vehicle.setYear(sales.getYear());
        vehicle.setInsurance(insuranceCompany);
        vehicle.setInsuranceNumber(sales.getInsuranceNumber());
        vehicle.setLoad(sales.getLoad());
        vehicle.setCapacity(sales.getCapacity());
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return sales;

    }

    @Override
    public List<InvoiceServiceType> getServiceTypeByCategory(Long salesId) {
        Sales sales = salesRepository.findById(salesId).get();
        List<InvoiceServiceType> invoiceServiceType = invoiceServiceTypeRepository.findByInvoice(sales.getInvoice());
        return invoiceServiceType;
    }

    @Override
    public InvoiceDto VehicleInvoice(Long id) {
        return null;
    }

    @Override
    public PortalUser createUser(UserDto dto) {
        Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
        PortalUser user = userManagementService.createUser(dto, jwtService.user, role);
        return user;
    }

    @Override
    public List<InvoiceServiceType> getServiceTypeByInvoiceId(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        List<InvoiceServiceType> invoiceServiceType = invoiceServiceTypeRepository.findByInvoice(invoice);
        return invoiceServiceType;
    }

    @Override
    public List<InsuranceCompany> getInsurance() {
        return insuranceRepository.findAll();
    }

    public VehicleDto viewVehicle(String chassisNo) {
        Optional<Vehicle> vehicle = Optional.ofNullable(vehicleRepository.findByChasisNumberIgnoreCase(chassisNo));

        VehicleDto dto = new VehicleDto();

        dto.setChasis(vehicle.get().getChasisNumber());
        dto.setEngine(vehicle.get().getEngineNumber());
        dto.setColor(vehicle.get().getColor());
        dto.setModel(vehicle.get().getVehicleModel().getName());
        dto.setMake(vehicle.get().getVehicleModel().getVehicleMake().getName());
        dto.setCategory(vehicle.get().getVehicleCategory().getName());
        dto.setCategoryId(vehicle.get().getVehicleCategory().getId());
        dto.setPlate(vehicle.get().getPlateNumber().getPlateNumber());

        return dto;
    }

    @Override
    public InvoiceDto getVehicleOwnerDetails(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        InvoiceDto invoiceDto = new InvoiceDto();

        invoiceDto.setFirstname(invoice.getPayer().getFirstName());
        invoice.setVehicle(invoice.getVehicle());
        invoiceDto.setPhonenumber(invoice.getPayer().getPhoneNumber());
        invoiceDto.setAddress(invoice.getPayer().getAddress());
        invoiceDto.setEmail(invoice.getPayer().getEmail());
        invoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDto.setReference(invoice.getPaymentRef());
        if(invoice.getVehicle() != null){
            invoiceDto.setCategory(invoice.getVehicle().getVehicleCategory().getName());
            invoiceDto.setPlatenumber(invoice.getVehicle().getPlateNumber().getPlateNumber());
            invoiceDto.setMake(invoice.getVehicle().getVehicleModel().getVehicleMake().getName());
            invoiceDto.setModel(invoice.getVehicle().getVehicleModel().getName());
            invoiceDto.setEngine(invoice.getVehicle().getEngineNumber());
            invoiceDto.setChasis(invoice.getVehicle().getChasisNumber());
        }
        invoiceDto.setDate(invoice.getCreatedAt());
        return invoiceDto;
    }
}
