package com.app.IVAS.dto;

import lombok.Data;

@Data
public class OffenseReportPojo {
    private String vio;
    private String taxPayer;
    private String plateNumber;
    private String offense;
    private String invoiceID;
    private String dateSold;
    private Double amount;
}
