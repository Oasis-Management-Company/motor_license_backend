package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VioSalesDto {
    private Long id;
    private String plate;
    private String chasis;
    private String engine;
    private String make;
    private String model;
    private String DisplayName;
    private String phoneNumber;
    private String invoiceNumber;
    private Double amount;
    private String paymentStatus;
    private String vioApproved;
    private String date;


}
