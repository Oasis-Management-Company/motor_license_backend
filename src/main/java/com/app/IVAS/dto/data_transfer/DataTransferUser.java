package com.app.IVAS.dto.data_transfer;

import lombok.Data;

@Data
public class DataTransferUser {
    private Long mla_id;
    private Long id;
    private String email;
    private String phone;
    private String station;
    private String name;
}
