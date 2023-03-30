package com.app.IVAS.controller;

import com.app.IVAS.dto.VerificationDto;
import com.app.IVAS.service.VerificationService;
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

    @GetMapping("/invoice/view/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceDetails(@PathVariable String invoiceNumber){

        return ResponseEntity.ok(verificationService.getScannedInvoice(invoiceNumber));
    }

}
