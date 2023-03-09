package com.app.IVAS.entity;


import com.app.IVAS.Enum.WorkFlowApprovalStatus;
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
@Table(name = "WORK_FLOW")
public class WorkFlow extends StatusEntity {
    @ManyToOne
    private WorkFlowStage stage;

    @ManyToOne
    private PortalUser finalApprover;

    @Enumerated(EnumType.STRING)
    private WorkFlowType type;

    @Enumerated(EnumType.STRING)
    private WorkFlowApprovalStatus workFlowApprovalStatus;
}
