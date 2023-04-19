package com.app.IVAS.service;

import com.app.IVAS.dto.PaymentDto;
import com.app.IVAS.dto.PaymentHistoryDto;

import java.io.IOException;
import java.util.List;

public interface PaymentService {
    String sendPaymentTax(String invoice);

    List<PaymentHistoryDto> verifyPayment(String invoice) throws IOException;
}
