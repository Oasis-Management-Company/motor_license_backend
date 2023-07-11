package com.app.IVAS.dto;

import com.app.IVAS.Enum.PlateNumberStatus;
import lombok.Data;

@Data
public class PlateNumberPojo {
    private Long id;
    private String plateNumber;
    private String type;
    private String subType;
    private String dateCreated;
    private PlateNumberStatus status;
    private String agent;
    private String owner;
    private String ministry;
}
