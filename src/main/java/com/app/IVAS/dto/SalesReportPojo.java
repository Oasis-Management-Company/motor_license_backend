package com.app.IVAS.dto;

import lombok.Data;

@Data
public class SalesReportPojo {
    private String mla;
    private String taxPayer;
    private String serviceType;
    private String invoiceID;
    private String mlaStation;
    private String dateSold;
    private Double amount;
}
