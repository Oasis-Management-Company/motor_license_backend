package com.app.IVAS.dto;

import com.app.IVAS.Enum.RegType;
import lombok.Data;

@Data
public class SalesReportPojo {
    private String mla;
    private String taxPayer;
    private String serviceType;
    private RegType regType;
    private String invoiceID;
    private String mlaStation;
    private String dateSold;
    private Double amount;
}
