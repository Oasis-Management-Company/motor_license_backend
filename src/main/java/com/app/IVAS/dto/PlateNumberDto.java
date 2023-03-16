package com.app.IVAS.dto;

import lombok.Data;

@Data
public class PlateNumberDto {
    private Long lgaId;
    private String endCode;
    private Long firstNumber;
    private Long lastNumber;
    private Long type;
    private Long subType;
}
