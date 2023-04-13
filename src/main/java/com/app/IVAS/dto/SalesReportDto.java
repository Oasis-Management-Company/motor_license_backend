package com.app.IVAS.dto;

import lombok.Data;

@Data
public class SalesReportDto {
    private String mla;
    private String plateNumber;
    private String mlaStation;
    private String dateSold;
    private Double amount;
}
