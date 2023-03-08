package com.app.IVAS.entity;


import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "WORK_FLOW_STAGE")
public class WorkFlowStage extends StatusEntity {

    private Long step;
    @ManyToOne
    private PortalUser ApprovingOfficer;

    private WorkFlowType type;
    private Boolean isFinalStage;
    private Boolean isSuperApprover;
}
