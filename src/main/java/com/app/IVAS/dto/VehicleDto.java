package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleDto {
    private String chasis;
    private String engine;
    private String category;
    private String weight;
    private  String make;
    private String model;
    private String year;
    private String color;
    private String plate;
    private LocalDateTime date;
}
