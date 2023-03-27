package com.app.IVAS.dto;

import com.app.IVAS.Enum.WorkFlowApprovalStatus;
import lombok.Data;

@Data
public class PlateNumberRequestPojo {
    private  Long id;
    private String trackingId;
    private String createdAt;
    private String createdBy;
    private String plateNumberType;
    private String PlateNumberSubType;
    private String NumberOfPlates;
    private WorkFlowApprovalStatus status;
    private String currentApprovingOfficer;
}
