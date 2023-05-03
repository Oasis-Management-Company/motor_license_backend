package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.*;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalesCtrlServiceImpl implements SalesCtrlService {

    private final VehicleRepository vehicleRepository;
    private final UserManagementService userManagementService;
    private final SalesRepository salesRepository;
    private final InvoiceRepository invoiceRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
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
    private final RrrGenerationService rrrGenerationService;
    private final PaymentServiceImpl paymentService;
    @Value("${asin_verification}")
    private String asinVerification;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
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
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(category, types, RegType.REGISTRATION, RegType.COMPULSARY);
        PortalUser portalUser = null;

        PortalUser user = portalUserRepository.findFirstByPhoneNumberOrEmail(sales.getPhone_number(), sales.getEmail());
        if (foundVehicle != null) {
            return null;
        }

        if (user == null) {
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
        } else {
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

        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();
        invoice.setPayer(portalUser);
        invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setAmount(totalAmount);
        invoice.setVehicle(savedVehicle);
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);


        for (ServiceType type : serviceTypes) {
            InvoiceServiceType serviceType = new InvoiceServiceType();
            serviceType.setServiceType(type);
            serviceType.setInvoice(savedInvoice);

            serviceType.setReference(invoiceNumber);
            if (type.getRevenueCode() != null){
                serviceType.setRevenuecode(type.getRevenueCode());
            }
            serviceType.setRegType(RegType.REGISTRATION);
            serviceType.setAmount(type.getPrice());
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
        cardService.createCard(savedInvoice, savedVehicle, RegType.REGISTRATION);

        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
        }

        return savedInvoice;
    }

    @Override
    public List<SalesDto> GetSales(List<Sales> results) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy/hh:mm:ss");
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
            dto.setMyDate(sales.getCreatedAt().format(df));
            dto.setAmount(sales.getInvoice().getAmount());
            dto.setStatus(sales.getInvoice().getPaymentStatus());
            dto.setApprovalStatus(sales.getApprovalStatus());
            dto.setCategoryId(sales.getVehicle().getVehicleCategory().getId());
            dto.setId(sales.getId());
            dto.setInvoice(sales.getInvoice().getId());
            dto.setPlatecat(sales.getVehicle().getPlateNumber().getType().getName());
            dto.setInvoiceNo(sales.getInvoice().getInvoiceNumber());
            return dto;

        }).collect(Collectors.toList());

    }

    @Override
    public SalesDto viewSale(Long id) {
        Optional<Sales> sale = salesRepository.findById(id);

        SalesDto dto = new SalesDto();
        dto.setFirstname(sale.get().getVehicle().getPortalUser().getDisplayName());
        dto.setAddress(sale.get().getVehicle().getPortalUser().getAddress());
        dto.setAsin(sale.get().getVehicle().getPortalUser().getAsin());
        dto.setEmail(sale.get().getVehicle().getPortalUser().getEmail());
        dto.setChasis(sale.get().getVehicle().getChasisNumber());
        dto.setEngine(sale.get().getVehicle().getEngineNumber());
        dto.setColor(sale.get().getVehicle().getColor());
        dto.setModel(sale.get().getVehicle().getVehicleModel().getName());
        dto.setMake(sale.get().getVehicle().getVehicleModel().getVehicleMake().getName());
        dto.setCategory(sale.get().getVehicle().getVehicleCategory().getName());
        dto.setPlate(sale.get().getVehicle().getPlateNumber().getPlateNumber());
        dto.setPlatetype(sale.get().getVehicle().getPlateNumber().getType().getId());
        dto.setMla(sale.get().getCreatedBy().getDisplayName());
        dto.setDate(sale.get().getCreatedAt());
        dto.setAmount(sale.get().getInvoice().getAmount());
        dto.setStatus(sale.get().getInvoice().getPaymentStatus());
        dto.setApprovalStatus(sale.get().getApprovalStatus());
        dto.setCategoryId(sale.get().getVehicle().getVehicleCategory().getId());
        dto.setId(sale.get().getId());
        dto.setInvoice(sale.get().getInvoice().getId());
        dto.setPlatecat(sale.get().getVehicle().getPlateNumber().getType().getName());
        dto.setInvoiceNo(sale.get().getInvoice().getInvoiceNumber());


        return dto;

    }

    @Override
    public PortalUser viewPortalUser(Long id) {
        Optional<PortalUser> taxPayer = portalUserRepository.findById(id);

        return taxPayer.get();
    }

    @Override
    public PortalUser editPortalUser(PortalUserPojo pojo) {
        Optional<PortalUser> existingUser = portalUserRepository.findById(pojo.getId());

        if (!existingUser.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        PortalUser existing = existingUser.get();

        PortalUser edit = new PortalUser();

        edit.setUsername(existing.getFirstName());
        edit.setParentUserName(existing.getUsername());
        if (pojo.getEmail().equalsIgnoreCase(existing.getEmail())) {
            edit.setParentEmail(existing.getEmail());
            edit.setEmail(pojo.getFirstName());
        } else {
            edit.setEmail(pojo.getEmail());
        }
        edit.setFirstName(pojo.getFirstName() != null ? pojo.getFirstName() : existing.getFirstName());
        edit.setLastName(pojo.getLastName() != null ? pojo.getLastName() : existing.getLastName());
        edit.setOtherNames(pojo.getOtherNames() != null ? pojo.getOtherNames() : existing.getOtherNames());
        edit.setParentId(existing.getId());
        edit.setAddress(pojo.getAddress() != null ? pojo.getAddress() : existing.getAddress());

        edit.setPhoneNumber(pojo.getPhoneNumber() != null ? pojo.getPhoneNumber() : existing.getPhoneNumber());
        edit.setAsin(pojo.getAsin() != null ? pojo.getAsin() : existing.getAsin());
        edit.setRegType(RegType.EDIT);
        edit.setCreatedBy(jwtService.user);
        edit.setCreatedAt(LocalDateTime.now());

        if (edit.getFirstName() != null && edit.getLastName() != null) {
            edit.setDisplayName(edit.getFirstName() + ' ' + edit.getLastName());
        } else if (edit.getFirstName() != null && edit.getLastName() == null) {
            edit.setDisplayName(edit.getFirstName());
        } else if (edit.getFirstName() == null && edit.getLastName() != null) {
            edit.setDisplayName(edit.getLastName());
        }

        portalUserRepository.save(edit);

        activityLogService.createActivityLog((edit.getDisplayName() + " detail was edited and sent for approval"), ActivityStatusConstant.UPDATE);

        return edit;
    }

    @Override
    public void approveTaxPayerEdit(Long id, String approval) {

        Optional<PortalUser> editedUser = portalUserRepository.findById(id);


        if (!editedUser.isPresent()) {
            throw new IllegalArgumentException("Edited User not found");
        }

        Optional<PortalUser> initialUser = portalUserRepository.findById(editedUser.get().getParentId());

        if (!initialUser.isPresent()) {
            throw new IllegalArgumentException("Initial User not found");
        }
        PortalUser edit = editedUser.get();
        PortalUser user = initialUser.get();

        if (approval.equalsIgnoreCase("Reject")) {
            portalUserRepository.delete(editedUser.get());
            activityLogService.createActivityLog((user.getDisplayName() + " edit request was disapproved"), ActivityStatusConstant.DISAPPROVAL);

        } else if (approval.equalsIgnoreCase("Approve")) {

            edit.setLastUpdatedAt(LocalDateTime.now());

            if (edit.getFirstName() != null) {
                user.setFirstName(edit.getFirstName());
            }
            if (edit.getLastName() != null) {
                user.setLastName(edit.getLastName());
            }

            if (edit.getAddress() != null) {
                user.setAddress(edit.getAddress());
            }

            if (edit.getAsin() != null) {
                user.setAsin(edit.getAsin());
            }
            if (edit.getPhoneNumber() != null) {
                user.setPhoneNumber(edit.getPhoneNumber());
            }

            if (edit.getParentEmail() != null) {
                user.setEmail(edit.getParentEmail());
            }

            if (edit.getOtherNames() != null) {
                user.setOtherNames(edit.getOtherNames());
            }

            if (edit.getFirstName() != null && edit.getLastName() != null) {
                user.setDisplayName(edit.getFirstName() + ' ' + edit.getLastName());
            } else if (edit.getFirstName() != null && edit.getLastName() == null) {
                user.setDisplayName(edit.getFirstName());
            } else if (edit.getFirstName() == null && edit.getLastName() != null) {
                user.setDisplayName(edit.getLastName());
            }

            portalUserRepository.save(user);

            portalUserRepository.delete(editedUser.get());

            activityLogService.createActivityLog((user.getDisplayName() + " edit request was approved and saved"), ActivityStatusConstant.APPROVAL);

        }


    }


    /**
     * Incomplete Edit function
     **/
//    @Override
//    public Void editSalesInvoice(SalesDto dto) {
//
//        Optional<Sales> initialSale = salesRepository.findById(dto.getId());
//        Invoice initialInvoice = initialSale.get().getInvoice();
//        Invoice duplicateInvoice = new Invoice();
//
//        duplicateInvoice.setEditCopy(true);
//        duplicateInvoice.setCreatedBy(jwtService.user);
//        duplicateInvoice.setStatus(GenericStatusConstant.INACTIVE);
//        duplicateInvoice.setParentInvoiceNumber(initialInvoice.getInvoiceNumber());
//        duplicateInvoice.setAmount(initialInvoice.getAmount());
//        duplicateInvoice.setPayer(initialInvoice.getPayer());
//        duplicateInvoice.setVehicle(initialInvoice.getVehicle());
//        duplicateInvoice.setPaymentDate(initialInvoice.getPaymentDate());
//        duplicateInvoice.setCreatedAt(LocalDateTime.now());
//        duplicateInvoice.setLastUpdatedAt(LocalDateTime.now());
//        duplicateInvoice.setLastUpdatedBy(jwtService.user);
//        duplicateInvoice.setPaymentRef(initialInvoice.getPaymentRef());
//
//        if(dto.getPlatetype() != null){
//            PlateNumberType type = plateNumberTypeRepository.findById(dto.getPlatetype()).get();
//        }
//
//
//
//    }
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
            responseRC = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, UserDemographyDto.class);

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
        List<PlateNumber> plateNumbers = plateNumberRepository.findAllByAgentAndPlateNumberStatusAndTypeAndOwnerIsNull(jwtService.user, PlateNumberStatus.ASSIGNED, type);
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
    public String getApprovalStatus(Long id, String action, Optional<String> reason) {
        Sales sales = salesRepository.findById(id).orElseThrow(RuntimeException::new);

        if (action.equalsIgnoreCase("APPROVED")) {
            sales.setApprovalStatus(ApprovalStatus.APPROVED);
            sales.setApprovedDate(LocalDateTime.now());
            sales.setApprovedBy(jwtService.user);
        } else if (action.equalsIgnoreCase("QUERIED")) {
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
            dto.setPlateType(sales.getPlateNumber().getType().getName());
            dto.setParent(sales.getId());
            return dto;

        }).collect(Collectors.toList());
    }


    @Override
    public SalesDto AddVehicle(SalesDto sales) {
        Vehicle vehicle = new Vehicle();
        UserDto dto = new UserDto();
        PlateNumber number = new PlateNumber();
        PortalUser portalUser = null;

        VehicleModel model = vehicleModelRepository.findById(sales.getModelId()).get();
        VehicleCategory category = vehicleCategoryRepository.findById(sales.getCategoryId()).get();
        PlateNumberType type = plateNumberTypeRepository.findById(sales.getPlatetype()).get();
        InsuranceCompany insuranceCompany = insuranceRepository.findById(sales.getInsurance()).get();
        portalUser = portalUserRepository.findFirstByPhoneNumber(sales.getPhone_number());

        if (portalUser == null) {
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

    @Override
    public VehicleDto viewVehicle(String chassisNo) {
        Vehicle vehicle = vehicleRepository.findById(Long.valueOf(chassisNo)).get();

        VehicleDto dto = new VehicleDto();

        dto.setChasis(vehicle.getChasisNumber());
        dto.setEngine(vehicle.getEngineNumber());
        dto.setColor(vehicle.getColor());
        dto.setModel(vehicle.getVehicleModel().getName());
        dto.setMake(vehicle.getVehicleModel().getVehicleMake().getName());
        dto.setCategory(vehicle.getVehicleCategory().getName());
        dto.setCategoryId(vehicle.getVehicleCategory().getId());
        dto.setPlate(vehicle.getPlateNumber().getPlateNumber());
        dto.setYear(vehicle.getYear());
        dto.setParent(vehicle.getId());

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
        if (invoice.getVehicle() != null) {
            invoiceDto.setCategory(invoice.getVehicle().getVehicleCategory().getName());
            invoiceDto.setPlatenumber(invoice.getVehicle().getPlateNumber().getPlateNumber());
            invoiceDto.setMake(invoice.getVehicle().getVehicleModel().getVehicleMake().getName());
            invoiceDto.setModel(invoice.getVehicle().getVehicleModel().getName());
            invoiceDto.setEngine(invoice.getVehicle().getEngineNumber());
            invoiceDto.setChasis(invoice.getVehicle().getChasisNumber());
            invoiceDto.setPlateType(invoice.getVehicle().getPlateNumber().getType().getName());
        }
        invoiceDto.setDate(invoice.getCreatedAt());
        return invoiceDto;
    }

    @Override
    public List<ServiceType> getServiceByCatandPlate(Long cat, Long plate) {

        VehicleCategory category = vehicleCategoryRepository.findById(cat).get();
        PlateNumberType plateNumber = plateNumberTypeRepository.findById(plate).get();
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(category, plateNumber, RegType.REGISTRATION, RegType.COMPULSARY);
        return serviceTypes;
    }

    @Override
    public Invoice SaveSalesEdit(SalesDto sales) {
        System.out.println(sales);
        List<InvoiceServiceType> invoiceServiceTypeArrayList = new ArrayList<>();
        Double totalAmount = 0.0;

        Invoice invoiceEdited = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(sales.getInvoiceNo());
        if (invoiceEdited == null){
            return null;
        }

        PlateNumberType types = plateNumberTypeRepository.findById(invoiceEdited.getVehicle().getPlateNumber().getType().getId()).get();
        VehicleCategory category = vehicleCategoryRepository.findById(sales.getCategoryId()).get();
        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndPlateNumberTypeAndRegTypeOrRegType(category, types, RegType.REGISTRATION, RegType.COMPULSARY);

        System.out.println(category);
        System.out.println(types);
        System.out.println(serviceTypes);
        List<InvoiceServiceType> servicesToBeDeleted = invoiceServiceTypeRepository.findByInvoice(invoiceEdited);

        for (InvoiceServiceType invoiceServiceType : servicesToBeDeleted) {
            invoiceServiceTypeRepository.delete(invoiceServiceType);
        }


        for (ServiceType type : serviceTypes) {
            totalAmount += type.getPrice();
        }

        invoiceEdited.setAmount(totalAmount);
        invoiceEdited.setLastUpdatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoiceEdited);

        for (ServiceType type : serviceTypes) {
            InvoiceServiceType serviceType = new InvoiceServiceType();
            serviceType.setServiceType(type);
            serviceType.setInvoice(savedInvoice);

            serviceType.setReference(invoiceEdited.getInvoiceNumber());
            if (type.getRevenueCode() != null){
                serviceType.setRevenuecode(type.getRevenueCode());
            }
            serviceType.setRegType(RegType.REGISTRATION);
            serviceType.setAmount(type.getPrice());
            invoiceServiceTypeArrayList.add(serviceType);
        }
        invoiceServiceTypeRepository.saveAll(invoiceServiceTypeArrayList);

        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
        }

        Vehicle vehicle = vehicleRepository.findById(invoiceEdited.getVehicle().getId()).get();
        vehicle.setVehicleCategory(category);

        vehicleRepository.save(vehicle);

        return savedInvoice;
    }
}
