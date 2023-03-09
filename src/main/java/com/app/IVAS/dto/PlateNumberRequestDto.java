package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlateNumberRequestDto {
    private Long numberOfPlates;
    private String plateType;
    private String plateSubType;
    private List<PreferredPlateDto> preferredPlates;
}
