package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QVehicleCategory is a Querydsl query type for VehicleCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVehicleCategory extends EntityPathBase<VehicleCategory> {

    private static final long serialVersionUID = 2132028948L;

    public static final QVehicleCategory vehicleCategory = new QVehicleCategory("vehicleCategory");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath weight = createString("weight");

    public QVehicleCategory(String variable) {
        super(VehicleCategory.class, forVariable(variable));
    }

    public QVehicleCategory(Path<? extends VehicleCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVehicleCategory(PathMetadata metadata) {
        super(VehicleCategory.class, metadata);
    }

}

