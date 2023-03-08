package com.app.IVAS.dto;

import lombok.Data;


@Data
public class PasswordDto {
    private String username;
    private String oldPassword;
    private String newPassword;
}
