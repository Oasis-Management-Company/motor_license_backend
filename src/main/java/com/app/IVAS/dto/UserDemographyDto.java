package com.app.IVAS.dto;

import com.app.IVAS.entity.userManagement.PortalUser;
import lombok.Data;

@Data
public class UserDemographyDto {
    private Long id;
    private PortalUser user;
    private String bvn;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String address;
    private String city;
    private Long lgaId;
    private Boolean legacy = false;
    private Long stateId;
    private String photo;
    private Long lga;
    private Long area;
    private String houseNumber;
    private String landMark;
    private String name;
    private String asin;
    private String dateCreated;
    private String email;
    private String middlename;
}
