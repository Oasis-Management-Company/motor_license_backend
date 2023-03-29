package com.app.IVAS.dto;

import com.app.IVAS.Enum.RegType;
import lombok.Data;

@Data
public class ServiceTypeDto {
    private String name;
    private Double price;
    private Long durationInMonth;
    private Long category;
    private RegType type;
    private Long plateNumberType;
}
