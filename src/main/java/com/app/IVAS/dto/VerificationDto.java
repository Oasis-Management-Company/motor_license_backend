package com.app.IVAS.dto;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.entity.ServiceType;
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

    private String invoiceNumber;
    private LocalDateTime paymentDate;
    private Double amount;
    private String paymentStatus;

    private List<InvoiceServiceTypeDto> invoiceServices;

    private List<ServiceType> serviceTypes;

    private LocalDateTime expiryDate;

    private String chasis;
    private String engine;
    private String category;
    private String weight;
    private  String make;
    private String model;
    private String year;
    private String color;
    private String plate;

    private String referenceNumber;


}
