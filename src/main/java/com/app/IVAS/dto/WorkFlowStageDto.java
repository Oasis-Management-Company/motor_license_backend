package com.app.IVAS.dto;

import com.app.IVAS.Enum.WorkFlowType;
import lombok.Data;

@Data
public class WorkFlowStageDto {
    private Long step;
    private Long ApprovingOfficer;
    private WorkFlowType type;
    private Boolean isFinalStage;
    private Boolean isSuperApprover;
}
