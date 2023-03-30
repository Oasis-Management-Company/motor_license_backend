package com.app.IVAS.dto;

import com.app.IVAS.Enum.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VerificationDto {

    String name;
    String phoneNumber;
    String address;
    String email;
    String asin;

    String invoiceNumber;
    LocalDateTime paymentDate;
    Double amount;
    String paymentStatus;

    List<InvoiceServiceTypeDto> invoiceServices;


}
