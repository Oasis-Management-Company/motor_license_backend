package com.app.IVAS.dto;

import lombok.Data;

@Data
public class StockPojo {
    private Long id;
    private String lga;
    private String range;
    private String endCode;
    private String type;
    private String subType;
    private String dateCreated;
    private String quantity;
    private String initialQuantity;
    private String createdBy;
}
