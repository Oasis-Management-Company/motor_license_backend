package com.app.IVAS.entity;


import com.app.IVAS.Enum.AssignmentStatusConstant;
import com.app.IVAS.Enum.WorkFlowApprovalStatus;
import com.app.IVAS.entity.userManagement.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PLATE_NUMBER_REQUEST")
public class PlateNumberRequest extends StatusEntity {

    private Long totalNumberRequested;
    private String trackingId;
    @ManyToOne
    private PlateNumberType plateNumberType;

    @ManyToOne
    private PlateNumberSubType subType;

    @OneToOne
    private WorkFlow workFlow;

    @Enumerated(EnumType.STRING)
    private WorkFlowApprovalStatus workFlowApprovalStatus;

    @Enumerated(EnumType.STRING)
    private AssignmentStatusConstant assignmentStatus;
}
