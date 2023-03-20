package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlateNumber is a Querydsl query type for PlateNumber
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumber extends EntityPathBase<PlateNumber> {

    private static final long serialVersionUID = 2057525449L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlateNumber plateNumber1 = new QPlateNumber("plateNumber1");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final com.app.IVAS.entity.userManagement.QPortalUser agent;

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

    public final com.app.IVAS.entity.userManagement.QPortalUser owner;

    public final StringPath plateNumber = createString("plateNumber");

    public final EnumPath<com.app.IVAS.Enum.PlateNumberStatus> plateNumberStatus = createEnum("plateNumberStatus", com.app.IVAS.Enum.PlateNumberStatus.class);

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final com.app.IVAS.entity.QStock stock;

    public final com.app.IVAS.entity.QPlateNumberSubType subType;

    public final com.app.IVAS.entity.QPlateNumberType type;

    public QPlateNumber(String variable) {
        this(PlateNumber.class, forVariable(variable), INITS);
    }

    public QPlateNumber(Path<? extends PlateNumber> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlateNumber(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlateNumber(PathMetadata metadata, PathInits inits) {
        this(PlateNumber.class, metadata, inits);
    }

    public QPlateNumber(Class<? extends PlateNumber> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.agent = inits.isInitialized("agent") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("agent"), inits.get("agent")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.owner = inits.isInitialized("owner") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("owner"), inits.get("owner")) : null;
        this.status = _super.status;
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock"), inits.get("stock")) : null;
        this.subType = inits.isInitialized("subType") ? new QPlateNumberSubType(forProperty("subType"), inits.get("subType")) : null;
        this.type = inits.isInitialized("type") ? new QPlateNumberType(forProperty("type")) : null;
    }

}

