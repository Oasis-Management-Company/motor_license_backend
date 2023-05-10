package com.app.IVAS.service;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.InsuranceResponse;

import java.io.IOException;
import java.util.List;

public interface PaymentService {
    String sendPaymentTax(String invoice);

    List<PaymentHistoryDto> verifyPayment(String invoice) throws IOException;

    AssessmentResponse PaymentReturn(PaymentResponse respondDto);

    InsuranceResponse sendInsuranceToVendor(String plate, String invoiceNumber);
}
