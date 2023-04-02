package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final ServiceTypeRepository serviceTypeRepository;


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
    public VehicleDto getVehicleDetailsByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicle(vehicle);
        vehicleDto.setFirstname(vehicle.getPortalUser().getFirstName() + " " + vehicle.getPortalUser().getLastName());
        vehicleDto.setEmail(vehicle.getPortalUser().getEmail());
        vehicleDto.setAddress(vehicle.getPortalUser().getAddress());
        vehicleDto.setPhonenumber(vehicle.getPortalUser().getPhoneNumber());

        return vehicleDto;
    }

    @Override
    public List<ServiceType> getServiceTypeByPlate(String plate) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);

        List<ServiceType> serviceTypes = serviceTypeRepository.findAllByCategoryAndRegTypeAndPlateNumberTypeOrCategoryAndPlateNumberTypeIsNull(vehicle.getVehicleCategory(), RegType.RENEWAL, plateNumber.getType(), vehicle.getVehicleCategory());
        return serviceTypes;
    }
}
