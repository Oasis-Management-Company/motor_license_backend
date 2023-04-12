package com.app.IVAS.dto;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDto {
    List<Invoice> invoices;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String email;
    private String address;
    private String asin;
    private String photo;
    List<Vehicle> vehicleDtos;
    private String platenumber;
    private String chasis;
    private String engine;
    private String make;
    private String model;
    private String category;
    private String invoiceNumber;
    private String reference;
    private LocalDateTime date;
    private String mla;
    private String plateType;
    private Double amount;
    private PaymentStatus status;
    private Long id;
}
