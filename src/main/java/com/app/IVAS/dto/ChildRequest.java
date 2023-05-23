package com.app.IVAS.dto;

import lombok.Data;

@Data
public class ChildRequest {
    private String name;
    private String description;
    private Double amount;
    private String itemCode;
}
