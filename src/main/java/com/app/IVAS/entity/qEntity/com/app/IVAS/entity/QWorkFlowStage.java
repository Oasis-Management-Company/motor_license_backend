package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkFlowStage is a Querydsl query type for WorkFlowStage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkFlowStage extends EntityPathBase<WorkFlowStage> {

    private static final long serialVersionUID = 1399680681L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkFlowStage workFlowStage = new QWorkFlowStage("workFlowStage");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final com.app.IVAS.entity.userManagement.QPortalUser ApprovingOfficer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    //inherited
    public final NumberPath<Long> id;

    public final BooleanPath isFinalStage = createBoolean("isFinalStage");

    public final BooleanPath isSuperApprover = createBoolean("isSuperApprover");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final NumberPath<Long> step = createNumber("step", Long.class);

    public final EnumPath<com.app.IVAS.Enum.WorkFlowType> type = createEnum("type", com.app.IVAS.Enum.WorkFlowType.class);

    public QWorkFlowStage(String variable) {
        this(WorkFlowStage.class, forVariable(variable), INITS);
    }

    public QWorkFlowStage(Path<? extends WorkFlowStage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkFlowStage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkFlowStage(PathMetadata metadata, PathInits inits) {
        this(WorkFlowStage.class, metadata, inits);
    }

    public QWorkFlowStage(Class<? extends WorkFlowStage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.ApprovingOfficer = inits.isInitialized("ApprovingOfficer") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("ApprovingOfficer"), inits.get("ApprovingOfficer")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.status = _super.status;
    }

}

