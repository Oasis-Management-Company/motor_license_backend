package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
public class AsinDto {
    private Long ninId;
    private Long lga;
    private Long area;
    private String streetName;
    private String houseNumber;
    private String landMark;;
    private String name;
    private String phoneNumber;
    private String asin;
    private LocalDateTime date;
    private String address;
    private String photo;
    private String dateCreated;
    private String email;
    private String firstname;
    private String lastname;
    private String othernames;
    private Date dob;
    private String gender;
    private String employmentStatus;
    private String middleName;
    private String maritalStatus;
    private String nin;
    private String profession;
    private String surname;
    private String telePhoneNumber;
    private String type;
}
