package com.app.IVAS.dto;

import com.app.IVAS.entity.VehicleMake;
import lombok.Data;

@Data
public class VehicleMakeAndModelDto {
    String makeName;
    String modelName;
    String modelYear;

}
