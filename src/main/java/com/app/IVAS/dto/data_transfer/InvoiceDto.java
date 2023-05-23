package com.app.IVAS.dto.data_transfer;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceDto {
    private String chassis_no;
    private String vehicle_owner_mobile_number;
    private Double amount;
    private LocalDate expiry_date;
    private String  service_name;
    private String  plate;
    private String transaction_reference;
    private LocalDateTime time_created;
}
