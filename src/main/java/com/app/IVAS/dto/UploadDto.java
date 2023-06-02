package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UploadDto {
    private Double amount;
    private String time_created;
    private String description;
    private String email;
    private String first_name;
    private String item_code;
    private String reference_number;
    private String cust_reference;
    private String date_booked;
    private String last_name;
    private String amvas_status;
}
