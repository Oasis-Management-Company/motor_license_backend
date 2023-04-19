package com.app.IVAS.dto;

import lombok.Data;

@Data
public class PaymentHistoryDto {
    private Float amount;
    private String payment_date;
    private String name;
    private String feeDescription;
    private String asin;
    private String paymentReference;
}
