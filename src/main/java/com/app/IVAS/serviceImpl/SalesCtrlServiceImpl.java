package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.UserDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.SalesCtrlService;
import com.app.IVAS.service.UserManagementService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
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


    @Override
    public Sales SaveSales(SalesDto sales) {
        Vehicle vehicle = new Vehicle();
        Sales sales1 = new Sales();
        UserDto dto = new UserDto();
        Invoice invoice = new Invoice();



//        VehicleMake make = vehicleMakeRepository.findById(sales.getVehicleMake()).get();
//        PlateNumberType type = plateNumberTypeRepository.findById(sales.getPlatetype()).get();
        VehicleModel model = vehicleModelRepository.findById(sales.getModelId()).get();
        PlateNumber number = plateNumberRepository.findById(sales.getPlatenumber()).get();
        VehicleCategory category = vehicleCategoryRepository.findById(sales.getCategoryId()).get();

        dto.setAddress(sales.getAddress());
        dto.setEmail(sales.getEmail());
        dto.setFirstName(sales.getFirstname());
        dto.setLastName(sales.getAddress());
        dto.setLga(73L);
        dto.setPhoneNumber(sales.getPhone_number());
        dto.setPassword("password");
        dto.setRole("GENERAL_USER");
        dto.setArea(67L);

        Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
        PortalUser portalUser = userManagementService.createUser(dto, jwtService.user, role);

        vehicle.setUser(portalUser);
        vehicle.setColor(sales.getColor());
        vehicle.setVehicleModel(model);
        vehicle.setChasisNumber(sales.getChasis());
        vehicle.setEngineNumber(sales.getEngine());
        vehicle.setVehicleCategory(category);
        vehicle.setPassengers(sales.getPassengers());
        vehicle.setPlateNumber(number);
        vehicle.setCreatedBy(jwtService.user);
        vehicle.setPolicySector(sales.getPolicy());
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        invoice.setPayer(portalUser);

        sales1.setVehicle(savedVehicle);
        sales1.setInvoice(invoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setStatus(GenericStatusConstant.ACTIVE);

        Sales savedSales = salesRepository.save(sales1);


        return savedSales;
    }

    @Override
    public List<SalesDto> GetSales(List<Sales> results) {

        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();
            dto.setFirstname(sales.getVehicle().getUser().getDisplayName());
            dto.setAddress(sales.getVehicle().getUser().getAddress());
            dto.setAsin(sales.getVehicle().getUser().getAsin());
            dto.setEmail(sales.getVehicle().getUser().getEmail());
            dto.setChasis(sales.getVehicle().getChasisNumber());
            dto.setEngine(sales.getVehicle().getEngineNumber());
            dto.setColor(sales.getVehicle().getColor());
            dto.setModel(sales.getVehicle().getVehicleModel().getName());
            dto.setMake(sales.getVehicle().getVehicleModel().getVehicleMake().getName());
            dto.setCategory(sales.getVehicle().getVehicleCategory().getName());

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

        ResponseEntity<UserDemographicIndividual> responseRC = null;
        UserDemographicIndividual userinfo1 = null;

        String url = asinVerification + asin;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        try {
            responseRC = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,entity, UserDemographicIndividual.class);

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
}
