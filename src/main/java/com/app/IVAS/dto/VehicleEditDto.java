package com.app.IVAS.dto;

import lombok.Data;

@Data
public class VehicleEditDto {
    private VehicleDto oldDetails;
    private VehicleDto newDetails;
}
