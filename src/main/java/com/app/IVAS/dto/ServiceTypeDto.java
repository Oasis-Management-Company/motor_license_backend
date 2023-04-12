package com.app.IVAS.dto;

import com.app.IVAS.Enum.RegType;
import lombok.Data;

@Data
public class ServiceTypeDto {
    private String name;
    private Double price;
    private Long durationInMonth;
    private Long category;
    private String categoryName;
    private RegType type;
    private Long plateNumberType;
    private String plateNumberTypeName;
    private Double amount;
    private String revenueCode;
}
