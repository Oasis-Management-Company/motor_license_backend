package com.app.IVAS.dto;

import com.app.IVAS.Enum.WorkFlowType;

import lombok.Data;


@Data
public class WorkFLowStagePojo {
    private Long step;
    private String ApprovingOfficer;
    private WorkFlowType type;
    private Boolean isFinalStage;
    private Boolean isSuperApprover;
    private String CreatedAt;
    private String CreatedBy;
}
