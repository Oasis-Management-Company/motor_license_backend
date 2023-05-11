package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVehicle is a Querydsl query type for Vehicle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVehicle extends EntityPathBase<Vehicle> {

    private static final long serialVersionUID = 569795318L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVehicle vehicle = new QVehicle("vehicle");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final StringPath capacity = createString("capacity");

    public final StringPath chasisNumber = createString("chasisNumber");

    public final StringPath color = createString("color");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final StringPath engineNumber = createString("engineNumber");

    //inherited
    public final NumberPath<Long> id;

    public final QInsuranceCompany insurance;

    public final StringPath insuranceNumber = createString("insuranceNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final StringPath load = createString("load");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final NumberPath<Long> passengers = createNumber("passengers", Long.class);

    public final StringPath permit = createString("permit");

    public final com.app.IVAS.entity.QPlateNumber plateNumber;

    public final StringPath policySector = createString("policySector");

    public final com.app.IVAS.entity.userManagement.QPortalUser portalUser;

    public final EnumPath<com.app.IVAS.Enum.RegType> regType = createEnum("regType", com.app.IVAS.Enum.RegType.class);

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final QVehicleCategory vehicleCategory;

    public final QVehicleModel vehicleModel;

    public final StringPath year = createString("year");

    public QVehicle(String variable) {
        this(Vehicle.class, forVariable(variable), INITS);
    }

    public QVehicle(Path<? extends Vehicle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVehicle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVehicle(PathMetadata metadata, PathInits inits) {
        this(Vehicle.class, metadata, inits);
    }

    public QVehicle(Class<? extends Vehicle> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.insurance = inits.isInitialized("insurance") ? new QInsuranceCompany(forProperty("insurance")) : null;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.plateNumber = inits.isInitialized("plateNumber") ? new QPlateNumber(forProperty("plateNumber"), inits.get("plateNumber")) : null;
        this.portalUser = inits.isInitialized("portalUser") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("portalUser"), inits.get("portalUser")) : null;
        this.status = _super.status;
        this.vehicleCategory = inits.isInitialized("vehicleCategory") ? new QVehicleCategory(forProperty("vehicleCategory")) : null;
        this.vehicleModel = inits.isInitialized("vehicleModel") ? new QVehicleModel(forProperty("vehicleModel"), inits.get("vehicleModel")) : null;
    }

}

