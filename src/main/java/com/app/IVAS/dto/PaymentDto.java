package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentDto {
    List<ExternalPaymentDto> serviceTypeDtos;
    private String ParentDescription;
    private Double TotalAmount;
    private String FirstName;
    private String LastName;
    private String email;
    private String TransactionId;

}
