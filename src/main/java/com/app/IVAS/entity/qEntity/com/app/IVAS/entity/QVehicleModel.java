package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVehicleModel is a Querydsl query type for VehicleModel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVehicleModel extends EntityPathBase<VehicleModel> {

    private static final long serialVersionUID = -1963978285L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVehicleModel vehicleModel = new QVehicleModel("vehicleModel");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QVehicleMake vehicleMake;

    public final StringPath year = createString("year");

    public QVehicleModel(String variable) {
        this(VehicleModel.class, forVariable(variable), INITS);
    }

    public QVehicleModel(Path<? extends VehicleModel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVehicleModel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVehicleModel(PathMetadata metadata, PathInits inits) {
        this(VehicleModel.class, metadata, inits);
    }

    public QVehicleModel(Class<? extends VehicleModel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vehicleMake = inits.isInitialized("vehicleMake") ? new QVehicleMake(forProperty("vehicleMake")) : null;
    }

}

