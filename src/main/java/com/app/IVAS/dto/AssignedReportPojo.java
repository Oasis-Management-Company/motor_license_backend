package com.app.IVAS.dto;

import com.app.IVAS.Enum.PlateNumberStatus;
import lombok.Data;

@Data
public class AssignedReportPojo {
    private Long id;
    private String plateNumber;
    private String type;
    private String subType;
    private String mla;
    private String dateAssigned;
    private PlateNumberStatus status;
}
