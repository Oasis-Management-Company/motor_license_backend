package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;


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
}
