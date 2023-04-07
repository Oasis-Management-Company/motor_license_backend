package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;


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

    @Override
    public Invoice saveServiceTypeByPlate(String myplate, List<Long> ids) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(myplate);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);
        Double totalAmount = 0.0;

        for (Long id : ids) {
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            totalAmount += serviceType.getPrice();
        }

        Invoice invoice = new Invoice();
        invoice.setVehicle(vehicle);
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setPaymentRef("IVS-" + LocalDate.now().getYear()+ (int)(Math.random()* 12345607));
        invoice.setAmount(totalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);



        for (Long id : ids) {
            InvoiceServiceType invoiceServiceType = new InvoiceServiceType();
            ServiceType serviceType = serviceTypeRepository.findById(id).get();
            invoiceServiceType.setServiceType(serviceType);
            invoiceServiceType.setInvoice(savedInvoice);
            invoiceServiceType.setRevenuecode(serviceType.getCode());
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }
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
}
