package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkFlow is a Querydsl query type for WorkFlow
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkFlow extends EntityPathBase<WorkFlow> {

    private static final long serialVersionUID = -1495994027L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkFlow workFlow = new QWorkFlow("workFlow");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final com.app.IVAS.entity.userManagement.QPortalUser finalApprover;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final QWorkFlowStage stage;

    public final EnumPath<com.app.IVAS.Enum.WorkFlowApprovalStatus> status = createEnum("status", com.app.IVAS.Enum.WorkFlowApprovalStatus.class);

    public final EnumPath<com.app.IVAS.Enum.WorkFlowType> type = createEnum("type", com.app.IVAS.Enum.WorkFlowType.class);

    public QWorkFlow(String variable) {
        this(WorkFlow.class, forVariable(variable), INITS);
    }

    public QWorkFlow(Path<? extends WorkFlow> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkFlow(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkFlow(PathMetadata metadata, PathInits inits) {
        this(WorkFlow.class, metadata, inits);
    }

    public QWorkFlow(Class<? extends WorkFlow> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.finalApprover = inits.isInitialized("finalApprover") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("finalApprover"), inits.get("finalApprover")) : null;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.stage = inits.isInitialized("stage") ? new QWorkFlowStage(forProperty("stage"), inits.get("stage")) : null;
    }

}

