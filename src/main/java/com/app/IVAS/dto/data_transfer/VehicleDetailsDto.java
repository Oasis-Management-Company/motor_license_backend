package com.app.IVAS.dto.data_transfer;

import com.app.IVAS.Enum.ApprovalStatus;
import com.app.IVAS.Enum.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VehicleDetailsDto {
private String plate;
    private String type;
    private String colour;
    private String chassis_no;
    private String engine_capacity;
    private String category;
    private String make;
    private String model;
    private String year;
    private String registration_center;
    private String registration_date;
    private String vehicle_owner_full_name;
    private String vehicle_owner_mobile_number;
    private String vehicle_owner_address;
    private String vehicle_owner_asin;
}
