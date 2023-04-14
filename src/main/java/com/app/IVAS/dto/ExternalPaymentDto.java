package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExternalPaymentDto {
    private String referenceNumber;
    private String dateBooked;
    private String dateExpired;
    private String itemCode;
    private Double amount;
    private String name;
}
