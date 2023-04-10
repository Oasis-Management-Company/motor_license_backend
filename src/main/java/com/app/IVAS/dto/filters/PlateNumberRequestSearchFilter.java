package com.app.IVAS.dto.filters;


import com.app.IVAS.entity.QPlateNumberRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Getter
@Setter
@NoArgsConstructor
public class PlateNumberRequestSearchFilter extends BaseSearchDto implements QuerydslBinderCustomizer<QPlateNumberRequest> {
    private String createdAfter;
    private String createdBefore;

    @Override
    public void customize(QuerydslBindings bindings, QPlateNumberRequest root) {
        bindings.bind(root.trackingId).as("trackingId").first((path, value) -> path.eq(value));
        bindings.bind(root.assignmentStatus).as("assigmentStatus").first((path, value) -> path.eq(value));
        bindings.bind(root.workFlowApprovalStatus).as("workFlowStatus").first((path, value) -> path.eq(value));
        bindings.bind(root.createdBy.displayName).as("name").first((path, value) -> path.containsIgnoreCase(value));
        bindings.bind(root.createdBy.office.zone.id).as("zone").first((path, value) -> path.eq(value));
        if (root.workFlow.stage.ApprovingOfficer != null){
            bindings.bind(root.workFlow.stage.ApprovingOfficer.displayName).as("approvingOfficer").first((path, value) -> path.containsIgnoreCase(value));
        }
        bindings.bind(root.plateNumberType.id).as("plateNumberTYpe").first((path, value) -> path.eq(value));
        bindings.including(root.trackingId, root.assignmentStatus, root.workFlow.stage, root.createdBy, root.plateNumberType, root.workFlowApprovalStatus);
    }
}
