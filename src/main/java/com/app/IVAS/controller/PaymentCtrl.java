package com.app.IVAS.controller;


import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentCtrl {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/send")
    public ResponseEntity<?> sendPaymentToCBS(@RequestParam String invoice){
        String dto = paymentService.sendPaymentTax(invoice);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/verify/payment")
    public List<PaymentHistoryDto> verifyPayment(@RequestParam String invoice) throws IOException {
        return paymentService.verifyPayment(invoice);
    }

    @PostMapping(value = "/respondse/payment")
    public HttpStatus PaymentReturn(PaymentResponse respondDto) {
         paymentService.PaymentReturn(respondDto);
        return HttpStatus.OK;
    }
}
