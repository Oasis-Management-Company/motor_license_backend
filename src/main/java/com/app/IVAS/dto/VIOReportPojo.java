package com.app.IVAS.dto;

import lombok.Data;

@Data
public class VIOReportPojo {
    private String name;
    private int assessmentDone;
    private Double totalAmount;
    private Double totalPaid;
    private Double totalOwed;
}
