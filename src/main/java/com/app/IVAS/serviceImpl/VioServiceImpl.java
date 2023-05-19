package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VioSalesDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VioServiceImpl implements VioService {

    private final OffenseRepository offenseRepository;
    private final PortalUserRepository portalUserRepository;
    private final InvoiceOffenceRepository invoiceOffenceRepository;
    private final InvoiceRepository invoiceRepository;
    private final SalesRepository salesRepository;
    private final RrrGenerationService rrrGenerationService;
    private final JwtService jwtService;
    private final VehicleRepository vehicleRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final PaymentServiceImpl paymentService;


    @Override
    public List<Offense> getAllOffenses() {
        return offenseRepository.findAll();
    }

    @Override
    @Transactional
    public Invoice saveOffenseTypeByPhonenumber(String phoneNumber, List<Long> ids) {
        PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(phoneNumber);
        Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);


        Double totalAmount = 0.0;
        Sales sales1 = new Sales();

        for (Long id : ids) {
            Offense offense = offenseRepository.findById(id).get();
            totalAmount += offense.getFine();
        }
        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();

        Invoice invoice = new Invoice();
        invoice.setPayer(vehicle.getPortalUser());
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setAmount(totalAmount);
        invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoice.setCreatedBy(jwtService.user);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (Long id : ids) {

            Offense offense = offenseRepository.findById(id).get();
            InvoiceOffenseType invoiceOffenseType = new InvoiceOffenseType();
            invoiceOffenseType.setOffense(offense);
            invoiceOffenseType.setInvoice(savedInvoice);
            invoiceOffenseType.setRegType(RegType.OFFENCE);
            invoiceOffenseType.setAmount(offense.getFine());
            invoiceOffenseType.setReference(invoiceNumber);
            invoiceOffenseType.setRevenuecode(offense.getRevenueCode());

            invoiceOffenceRepository.save(invoiceOffenseType);
        }

        try {
            paymentService.sendPaymentTax(savedInvoice.getInvoiceNumber());
        } catch (Exception e) {
        }
        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.OFFENCE);
        sales1.setVehicle(vehicle);
        sales1.setInvoice(savedInvoice);
        salesRepository.save(sales1);
        return savedInvoice;
    }

    @Override
    public List<InvoiceOffenseType> getOffenseTypeByInvoice(Long salesId) {
        Invoice invoice = invoiceRepository.findById(salesId).get();
        List<InvoiceOffenseType> invoiceServiceType = invoiceOffenceRepository.findByInvoice(invoice);
        return invoiceServiceType;
    }

    @Override
    public PortalUser getOffensePayer(String phoneNumber) {
        return portalUserRepository.findFirstByPhoneNumber(phoneNumber);
    }

    @Override
    public List<SalesDto> searchAllForVIO(List<Sales> results) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy/hh:mm:ss");

        return results.stream().map(sales -> {
            SalesDto dto = new SalesDto();

            dto.setFirstname(sales.getInvoice().getPayer().getDisplayName());
            dto.setAddress(sales.getInvoice().getPayer().getAddress());
            dto.setAsin(sales.getInvoice().getPayer().getAsin());
            dto.setEmail(sales.getInvoice().getPayer().getEmail());
            dto.setPhone_number(sales.getInvoice().getPayer().getPhoneNumber());
            dto.setInvoiceNo(sales.getInvoice().getInvoiceNumber());
            dto.setMyDate(sales.getCreatedAt().format(df));
            dto.setAmount(sales.getInvoice().getAmount());
            dto.setStatus(sales.getInvoice().getPaymentStatus());
            dto.setApprovalStatus(sales.getApprovalStatus());
            dto.setId(sales.getId());
            dto.setInvoice(sales.getInvoice().getId());
            dto.setMake(sales.getVehicle().getVehicleModel().getVehicleMake().getName());
            dto.setModel(sales.getVehicle().getVehicleModel().getName());
            dto.setChasis(sales.getVehicle().getChasisNumber());
            dto.setEngine(sales.getVehicle().getEngineNumber());
            dto.setYear(sales.getVehicle().getYear());
            dto.setStatus(sales.getInvoice().getPaymentStatus());
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<VioSalesDto> get(List<Invoice> invoices) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        return invoices.stream().map(invoice -> {
            VioSalesDto dto = new VioSalesDto();
            dto.setId(invoice.getId());
            dto.setDisplayName(invoice.getPayer().getDisplayName());
            dto.setPlate(invoice.getVehicle().getPlateNumber().getPlateNumber());
            dto.setChasis(invoice.getVehicle().getChasisNumber());
            dto.setEngine(invoice.getVehicle().getEngineNumber());
            dto.setMake(invoice.getVehicle().getVehicleModel().getVehicleMake().getName());
            dto.setModel(invoice.getVehicle().getVehicleModel().getName());
            dto.setPhoneNumber(invoice.getPayer().getPhoneNumber());
            dto.setDate(invoice.getCreatedAt().format(df));
            dto.setInvoiceNumber(invoice.getInvoiceNumber());
            dto.setAmount(invoice.getAmount());
            dto.setPaymentStatus(invoice.getPaymentStatus().toString());
            dto.setVioApproved(invoice.getVioApproval().toString());

            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public Invoice approveInvoice(Long id) {

       Optional<Invoice> invoice = invoiceRepository.findById(id);

       invoice.get().setVioApproval(true);

       invoiceRepository.save(invoice.get());

       return invoice.get();

    }


}
