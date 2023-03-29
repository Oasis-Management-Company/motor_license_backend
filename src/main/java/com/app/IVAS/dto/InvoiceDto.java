package com.app.IVAS.dto;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import lombok.Data;

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
}
