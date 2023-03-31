package com.app.IVAS.dto;

import com.app.IVAS.Enum.AssignmentStatusConstant;
import com.app.IVAS.Enum.WorkFlowApprovalStatus;
import lombok.Data;

@Data
public class PlateNumberRequestPojo {
    private  Long id;
    private Long mlaId;
    private Long typeId;
    private Long subTypeId;
    private String trackingId;
    private String createdAt;
    private String createdBy;
    private String plateNumberType;
    private String PlateNumberSubType;
    private String NumberOfPlates;
    private WorkFlowApprovalStatus status;
    private AssignmentStatusConstant assignmentStatus;
    private String currentApprovingOfficer;
}
