package com.app.IVAS.dto;

import com.app.IVAS.Enum.ApprovalStatus;
import com.app.IVAS.Enum.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalesDto {

    private String asin;
    private String email;
    private String address;
    private String chasis;
    private String engine;
    private Long modelId;
    private String model;
    private String make;
    private String category;
    private Long categoryId;
    private String sector;
    private String color;
    private Long passengers;
    private String weight;
    private String capacity;
    private String load;
    private Long type;
    private String firstname;
    private String lastname;
    private String phone_number;
    private Long platenumber;
    private Long platetype;
    private String policy;
    private String vehicleChasis;
    private String vehicleEngine;
    private Long vehicleMake;
    private Long vehicleModel;
    private String year;
    private String plate;
    private String mla;
    private LocalDateTime date;
    private String myDate;
    private Double amount;
    private PaymentStatus status;
    private ApprovalStatus approvalStatus;
    private Long id;
    private Long insurance;
    private Long invoice;
    private String insuranceNumber;
    private String platecat;
    private String invoiceNo;
    private String qrCode;

    private String selectInsurance;

}
