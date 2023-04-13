package com.app.IVAS.controller;

import com.app.IVAS.dto.VerificationDto;
import com.app.IVAS.service.PaymentService;
import com.app.IVAS.service.VerificationService;
import com.app.IVAS.serviceImpl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class VerificationController {

    private final VerificationService verificationService;
    private final PaymentService paymentService;

    @GetMapping("/invoice/view/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceDetails(@PathVariable String invoiceNumber){

        paymentService.sendPaymentTax("4184919028224");
        return ResponseEntity.ok(verificationService.getScannedInvoice(invoiceNumber));
    }

}
