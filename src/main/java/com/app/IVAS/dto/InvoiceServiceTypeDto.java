package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvoiceServiceTypeDto extends ServiceTypeDto {

    String vehicleCategory;
    String reference;
    LocalDateTime paymentDate;
    LocalDateTime expiryDate;
    String expiry;

}
