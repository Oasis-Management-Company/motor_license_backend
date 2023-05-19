package com.app.IVAS.entity;


import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.StatusEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser ApprovingOfficer;

    @Enumerated(EnumType.STRING)
    private WorkFlowType type;

    private Boolean isFinalStage;
    private Boolean isSuperApprover;
}
