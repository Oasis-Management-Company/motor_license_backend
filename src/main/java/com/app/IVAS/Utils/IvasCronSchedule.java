package com.app.IVAS.Utils;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.PaymentService;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("default")
public class IvasCronSchedule {

    private final ExecutorService cronExecutor = Executors.newSingleThreadExecutor();
    private final PaymentService paymentService;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final InvoiceRepository invoiceRepository;
    private final AppRepository appRepository;

    @PostConstruct
    private void init() {
    }

//    @Scheduled(fixedDelay = 3600 * DateUtils.MILLIS_PER_SECOND, initialDelay = DateUtils.MILLIS_PER_MINUTE)
//    public void sendVerificationTransaction() {
//        cronExecutor.execute(() -> {
//            try {
//                cardService.deleteFiles();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

//    @Scheduled(fixedDelay = 36000)
//    public void sendVerificationTransaction() {
//        cronExecutor.execute(() -> {
//            try {
//                JPAQuery<Invoice> allInvoices = appRepository.startJPAQuery(QInvoice.invoice)
//                        .where(QInvoice.invoice.createdAt.after(LocalDateTime.now().minusDays(2)));
//
//                for (Invoice fetch : allInvoices.fetch()) {
//                    paymentService.sendPaymentTax(fetch.getInvoiceNumber());
//                    System.out.println("Send to tax" + fetch.getInvoiceNumber());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }

    @PreDestroy
    public void deInit(){this.cronExecutor.shutdown();}
}
