package com.app.IVAS.dto;

import com.app.IVAS.Enum.RegType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceTypeDto {
    private String name;
    private Double price;
    private Long durationInMonth;
    private Long category;
    private RegType type;
    private Long plateNumberType;
    private Double amount;
    private String revenueCode;
    private String transactionId;

    private String referenceNumber;
    private LocalDateTime dateBooked;
    private LocalDateTime dateExpired;
    private String itemCode;
}
