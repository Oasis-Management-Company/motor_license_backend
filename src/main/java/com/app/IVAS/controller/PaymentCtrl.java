package com.app.IVAS.controller;


import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.PaymentDto;
import com.app.IVAS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")
public class PaymentCtrl {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/send")
    public ResponseEntity<String> sendPaymentToCBS(@RequestParam String invoice){
        String dto = paymentService.sendPaymentTax(invoice);
        return ResponseEntity.ok(dto);
    }
}
