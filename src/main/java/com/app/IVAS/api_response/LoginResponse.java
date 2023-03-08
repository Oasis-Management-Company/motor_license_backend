package com.app.IVAS.api_response;

import com.app.IVAS.Enum.PermissionTypeConstant;
import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String firstName;
    private String lastName;
    private String image;
    private String token;
    private String role;
    private List<PermissionTypeConstant> permissions;
    private String username;
}
