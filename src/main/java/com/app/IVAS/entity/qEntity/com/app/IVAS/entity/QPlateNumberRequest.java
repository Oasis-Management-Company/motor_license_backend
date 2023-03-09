package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlateNumberRequest is a Querydsl query type for PlateNumberRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumberRequest extends EntityPathBase<PlateNumberRequest> {

    private static final long serialVersionUID = 1941794182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlateNumberRequest plateNumberRequest = new QPlateNumberRequest("plateNumberRequest");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

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

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final QPlateNumberType plateNumberType;

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final QPlateNumberSubType subType;

    public final NumberPath<Long> totalNumberRequested = createNumber("totalNumberRequested", Long.class);

    public final StringPath trackingId = createString("trackingId");

    public final QWorkFlow workFlow;

    public final EnumPath<com.app.IVAS.Enum.WorkFlowApprovalStatus> workFlowApprovalStatus = createEnum("workFlowApprovalStatus", com.app.IVAS.Enum.WorkFlowApprovalStatus.class);

    public QPlateNumberRequest(String variable) {
        this(PlateNumberRequest.class, forVariable(variable), INITS);
    }

    public QPlateNumberRequest(Path<? extends PlateNumberRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlateNumberRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlateNumberRequest(PathMetadata metadata, PathInits inits) {
        this(PlateNumberRequest.class, metadata, inits);
    }

    public QPlateNumberRequest(Class<? extends PlateNumberRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.plateNumberType = inits.isInitialized("plateNumberType") ? new QPlateNumberType(forProperty("plateNumberType")) : null;
        this.status = _super.status;
        this.subType = inits.isInitialized("subType") ? new QPlateNumberSubType(forProperty("subType"), inits.get("subType")) : null;
        this.workFlow = inits.isInitialized("workFlow") ? new QWorkFlow(forProperty("workFlow"), inits.get("workFlow")) : null;
    }

}

