package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlateNumberSales is a Querydsl query type for PlateNumberSales
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumberSales extends EntityPathBase<PlateNumberSales> {

    private static final long serialVersionUID = 963712451L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlateNumberSales plateNumberSales = new QPlateNumberSales("plateNumberSales");

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

    public final QInvoice invoice;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final StringPath referenceNumber = createString("referenceNumber");

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final QVehicle vehicle;

    public QPlateNumberSales(String variable) {
        this(PlateNumberSales.class, forVariable(variable), INITS);
    }

    public QPlateNumberSales(Path<? extends PlateNumberSales> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlateNumberSales(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlateNumberSales(PathMetadata metadata, PathInits inits) {
        this(PlateNumberSales.class, metadata, inits);
    }

    public QPlateNumberSales(Class<? extends PlateNumberSales> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
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

