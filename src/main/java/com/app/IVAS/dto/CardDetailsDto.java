package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardDetailsDto {

    private String invoiceNumber;

/** Status**/
    private Boolean valid;
    private String placeOfIssue;


/** Dates **/
    private LocalDateTime paymentDate;
    private LocalDateTime entryDate;
    private LocalDateTime registrationDate;
    private LocalDateTime expiryDate;


/** Individual Details **/
    private String image;
    private String lga;
    private String area;
    private String firstName;
    private String lastName;
    private String asin;
    private String email;
    private String phoneNumber;


/**   Vehicle Details **/
    private String chasisNumber;
    private String engineNumber;
    private String plateNumber;
    private String vehicleModel;
    private String vehicleCategory;
    private String policySector;
    private Long passengers;
    private String color;
    private String vehicleYear;
    private String weight;


/**   Insurance Details **/
    private String insuranceCompany;
    private String insurancePolicy;

}
