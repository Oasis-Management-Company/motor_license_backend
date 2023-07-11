package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.dto.VioDashboardDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceOffenseType;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VioDashboardService;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VioDashboardServiceImpl implements VioDashboardService {

    private final JwtService jwtService;
    private final AppRepository appRepository;
    @Override
    public VioDashboardDto VioDashboardReport() {

        Double rw = 0.0;
        Double dtt = 0.0;
        Double permit = 0.0;
        Long vehicles = 0L;
        Long approval  = 0L;
        Double offense = 0.0;


        JPAQuery<InvoiceServiceType> rwJpa = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("ROADWORTHINESS"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<InvoiceServiceType> lpJpa = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("LEARNERS PERMIT"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<InvoiceServiceType> dttJpa = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.contains("DRIVERS TEST THEORY"))
                .where(QInvoiceServiceType.invoiceServiceType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        JPAQuery<InvoiceOffenseType> offenseJPA = appRepository.startJPAQuery(QInvoiceOffenseType.invoiceOffenseType)
                .where(QInvoiceOffenseType.invoiceOffenseType.invoice.paymentStatus.eq(PaymentStatus.PAID));

        for (InvoiceServiceType fetch : rwJpa.fetch()) rw += fetch.getAmount();
        for (InvoiceServiceType fetch : lpJpa.fetch()) permit += fetch.getAmount();
        for (InvoiceServiceType fetch : dttJpa.fetch()) dtt += fetch.getAmount();
        for (InvoiceOffenseType fetch : offenseJPA.fetch()) offense += fetch.getAmount();

        var vio = VioDashboardDto.builder()
                .rw(rw)
                .offence(offense)
                .approvals(approval)
                .dtt(dtt)
                .permit(permit)
                .vehicles(vehicles)
                .build();


        return vio;
    }

}
