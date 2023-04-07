package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QServiceType is a Querydsl query type for ServiceType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QServiceType extends EntityPathBase<ServiceType> {

    private static final long serialVersionUID = 706692697L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QServiceType serviceType = new QServiceType("serviceType");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final QVehicleCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final NumberPath<Long> durationInMonth = createNumber("durationInMonth", Long.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final StringPath name = createString("name");

    public final QPlateNumberType plateNumberType;

    public final NumberPath<Double> price = createNumber("price", Double.class);

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final EnumPath<com.app.IVAS.Enum.RegType> type = createEnum("type", com.app.IVAS.Enum.RegType.class);

    public QServiceType(String variable) {
        this(ServiceType.class, forVariable(variable), INITS);
    }

    public QServiceType(Path<? extends ServiceType> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QServiceType(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QServiceType(PathMetadata metadata, PathInits inits) {
        this(ServiceType.class, metadata, inits);
    }

    public QServiceType(Class<? extends ServiceType> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QVehicleCategory(forProperty("category")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.plateNumberType = inits.isInitialized("plateNumberType") ? new QPlateNumberType(forProperty("plateNumberType")) : null;
        this.status = _super.status;
    }

}

