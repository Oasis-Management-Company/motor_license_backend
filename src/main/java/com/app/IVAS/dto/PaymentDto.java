package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentDto {
    List<ServiceTypeDto> serviceTypeDtos;
    private String description;
    private Double totalamount;
    private String firstname;
    private String lastname;
    private String email;
    private String transactionId;

}
