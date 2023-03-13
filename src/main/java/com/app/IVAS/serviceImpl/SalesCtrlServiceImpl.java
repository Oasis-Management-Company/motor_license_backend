package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.UserDto;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.Role;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.SalesCtrlService;
import com.app.IVAS.service.UserManagementService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public Sales SaveSales(SalesDto sales) {
        Vehicle vehicle = new Vehicle();
        Sales sales1 = new Sales();
        UserDto dto = new UserDto();


        Role role = roleRepository.findByNameIgnoreCase(dto.getRole()).orElseThrow(RuntimeException::new);
        PortalUser portalUser = userManagementService.createUser(dto, jwtService.user, role);

        vehicle.setUser(portalUser);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        sales1.setVehicle(savedVehicle);
        Sales savedSales = salesRepository.save(sales1);


        return savedSales;
    }

    @Override
    public List<SalesDto> GetSales(List<Sales> results) {

        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();
            dto.setFn(sales.getVehicle().getUser().getDisplayName());
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
}
