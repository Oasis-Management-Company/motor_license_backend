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
    private Long quantity;
    private Long initialQuantity;
    private String createdBy;
    private Long assigned;
    private Long sold;
    private String ministry;
}
