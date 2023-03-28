package com.app.IVAS.dto;

import lombok.Data;

@Data
public class ServiceTypeDto {
    private String name;
    private Double price;
    private Long durationInMonth;
    private Long category;
}
