package com.app.IVAS.dto;


import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.entity.userManagement.Role;
import lombok.Data;

@Data
public class PortalUserPojo {
    private Long id;
    private String name;
    private String email;
    private String dateCreated;
    private Role role;
    private GenericStatusConstant status;
}