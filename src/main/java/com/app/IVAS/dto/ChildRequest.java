package com.app.IVAS.dto;

import lombok.Data;

@Data
public class ChildRequest {
    private String referenceNumber;
    private String dateBooked;
    private String dateExpired;
    private String itemCode;
    private Double amount;
    private String name;
    private String description;
    private String custReference;
}
