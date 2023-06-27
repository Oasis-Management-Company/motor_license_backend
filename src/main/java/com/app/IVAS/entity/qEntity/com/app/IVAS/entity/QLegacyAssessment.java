package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLegacyAssessment is a Querydsl query type for LegacyAssessment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLegacyAssessment extends EntityPathBase<LegacyAssessment> {

    private static final long serialVersionUID = -1932890399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLegacyAssessment legacyAssessment = new QLegacyAssessment("legacyAssessment");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final EnumPath<com.app.IVAS.Enum.ApprovalStatus> approvalStatus = createEnum("approvalStatus", com.app.IVAS.Enum.ApprovalStatus.class);

    public final com.app.IVAS.entity.userManagement.QPortalUser approvedBy;

    public final DateTimePath<java.time.LocalDateTime> approvedDate = createDateTime("approvedDate", java.time.LocalDateTime.class);

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

    public final QInvoice invoice;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final EnumPath<com.app.IVAS.Enum.RegType> plateType = createEnum("plateType", com.app.IVAS.Enum.RegType.class);

    public final StringPath reason = createString("reason");

    public final StringPath reference_no = createString("reference_no");

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final QVehicle vehicle;

    public QLegacyAssessment(String variable) {
        this(LegacyAssessment.class, forVariable(variable), INITS);
    }

    public QLegacyAssessment(Path<? extends LegacyAssessment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLegacyAssessment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLegacyAssessment(PathMetadata metadata, PathInits inits) {
        this(LegacyAssessment.class, metadata, inits);
    }

    public QLegacyAssessment(Class<? extends LegacyAssessment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.approvedBy = inits.isInitialized("approvedBy") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("approvedBy"), inits.get("approvedBy")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.invoice = inits.isInitialized("invoice") ? new QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.status = _super.status;
        this.vehicle = inits.isInitialized("vehicle") ? new QVehicle(forProperty("vehicle"), inits.get("vehicle")) : null;
    }

}

