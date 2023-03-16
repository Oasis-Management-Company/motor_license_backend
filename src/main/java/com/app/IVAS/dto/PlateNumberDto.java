package com.app.IVAS.dto;

import lombok.Data;

@Data
public class PlateNumberDto {
    private String start;
    private String end;
    private int firstNumber;
    private int lastNumber;
}
