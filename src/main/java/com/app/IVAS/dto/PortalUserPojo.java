package com.app.IVAS.dto;


import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.entity.userManagement.Role;
import lombok.Data;

import java.util.Date;

@Data
public class PortalUserPojo {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String dateCreated;
    private Role role;
    private GenericStatusConstant status;
    private String dateOfBirth;
    private String address;
    private String asin;
    private Long parentId;
    private String parentEmail;
    private String firstName;
    private String lastName;
    private String otherNames;
    private Double amount;
    private String invoiceNo;
}