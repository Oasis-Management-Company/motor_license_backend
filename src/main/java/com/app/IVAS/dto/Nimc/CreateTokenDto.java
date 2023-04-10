package com.app.IVAS.dto.Nimc;

import lombok.Data;

@Data
public class CreateTokenDto {
    private String username;
    private String password;
    private String orgId;
    private String token;
}
