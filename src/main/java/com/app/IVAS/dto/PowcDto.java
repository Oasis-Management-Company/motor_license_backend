package com.app.IVAS.dto;

import lombok.Data;

@Data
public class PowcDto {
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String plateNumber;
    private String chassisNumber;
    private String engineNumber;
    private String vehicleType;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleCategory;
    private String netWeight;
    private String loadWeight;
    private String serviceName;
    private String serviceCode;
    private String serviceType;
    private String serviceCategory;
    private String cost;
    private String tx;
    private String status;
    private String paidAt;
    private String expiry;
    private String paymentType;
    private String paymentMode;
    private String currency;
    private String centre;
    private String dataSource;
    private int durationInMonth;
}
