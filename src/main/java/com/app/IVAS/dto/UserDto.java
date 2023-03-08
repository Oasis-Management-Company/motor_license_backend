package com.app.IVAS.dto;

import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String photo;
    private String email;
    private Long lga;
    private Long area;
    private String nin;
    private String password;
    private String role;
    private Long zonalOffice;
}
