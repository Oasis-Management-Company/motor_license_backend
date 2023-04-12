package com.app.IVAS.dto;

import lombok.Data;

@Data
public class StockReportPojo {
    private String mla;
    private String station;
    private int assigned;
    private int sold;
    private int currentQuantity;
}