package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VioServiceImpl implements VioService {

    private final OffenseRepository offenseRepository;
    private final PortalUserRepository portalUserRepository;
    private final InvoiceOffenceRepository invoiceOffenceRepository;
    private final InvoiceRepository invoiceRepository;
    private final SalesRepository salesRepository;
    private final RrrGenerationService rrrGenerationService;
    private final JwtService jwtService;



    @Override
    public List<Offense> getAllOffenses() {
        return offenseRepository.findAll();
    }

    @Override
    public Invoice saveOffenseTypeByPhonenumber(String phoneNumber, List<Long> ids) {
        PortalUser user = portalUserRepository.findFirstByPhoneNumber(phoneNumber);

        Double totalAmount = 0.0;
        Sales sales1 = new Sales();

        for (Long id : ids) {
            Offense offense = offenseRepository.findById(id).get();
            totalAmount += offense.getFine();
        }
        String invoiceNumber = rrrGenerationService.generateNewRrrNumber();

        Invoice invoice = new Invoice();
        invoice.setPayer(user);
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


        sales1.setInvoice(savedInvoice);
        sales1.setCreatedBy(jwtService.user);
        sales1.setPlateType(RegType.OFFENCE);
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
            return dto;

        }).collect(Collectors.toList());
    }

}
