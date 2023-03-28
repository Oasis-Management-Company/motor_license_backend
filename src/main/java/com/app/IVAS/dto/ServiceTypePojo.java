package com.app.IVAS.dto;

import lombok.Data;

@Data
public class ServiceTypePojo {
    private String name;
    private Double price;
    private Long durationInMonth;
    private String categoryName;
    private String createdAt;
    private String createdBy;
}
