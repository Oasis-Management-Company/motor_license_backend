package com.app.IVAS.dto;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Invoice> invoices;
    private Vehicle vehicle;
    private String insurance;
    private String company;


    private String firstname;
    private String lastname;
    private String address;
    private String phonenumber;
    private String email;

    private Long categoryId;

}
