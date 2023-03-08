package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
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

    public final StringPath chasisNumber = createString("chasisNumber");

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

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final NumberPath<Long> passengers = createNumber("passengers", Long.class);

    public final QPlateNumber plateNumber;

    public final StringPath policySector = createString("policySector");

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final com.app.IVAS.entity.userManagement.QPortalUser User;

    public final QVehicleCategory vehicleCategory;

    public final QVehicleModel vehicleModel;

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
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.plateNumber = inits.isInitialized("plateNumber") ? new QPlateNumber(forProperty("plateNumber"), inits.get("plateNumber")) : null;
        this.status = _super.status;
        this.User = inits.isInitialized("User") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("User"), inits.get("User")) : null;
        this.vehicleCategory = inits.isInitialized("vehicleCategory") ? new QVehicleCategory(forProperty("vehicleCategory")) : null;
        this.vehicleModel = inits.isInitialized("vehicleModel") ? new QVehicleModel(forProperty("vehicleModel"), inits.get("vehicleModel")) : null;
    }

}

