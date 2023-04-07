package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QVehicleMake is a Querydsl query type for VehicleMake
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVehicleMake extends EntityPathBase<VehicleMake> {

    private static final long serialVersionUID = -1448840700L;

    public static final QVehicleMake vehicleMake = new QVehicleMake("vehicleMake");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QVehicleMake(String variable) {
        super(VehicleMake.class, forVariable(variable));
    }

    public QVehicleMake(Path<? extends VehicleMake> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVehicleMake(PathMetadata metadata) {
        super(VehicleMake.class, metadata);
    }

}

