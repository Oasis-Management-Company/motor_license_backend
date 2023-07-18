package com.app.IVAS.dto;

import com.app.IVAS.entity.InvoiceServiceType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private String mla;
    private String category;
    private List<InvoiceServiceType> invoiceServiceTypeList;
    private String approvedBy;


}
