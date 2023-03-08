package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlateNumberSubType is a Querydsl query type for PlateNumberSubType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumberSubType extends EntityPathBase<PlateNumberSubType> {

    private static final long serialVersionUID = -1022419823L;

    public static final QPlateNumberSubType plateNumberSubType = new QPlateNumberSubType("plateNumberSubType");

    public QPlateNumberSubType(String variable) {
        super(PlateNumberSubType.class, forVariable(variable));
    }

    public QPlateNumberSubType(Path<? extends PlateNumberSubType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlateNumberSubType(PathMetadata metadata) {
        super(PlateNumberSubType.class, metadata);
    }

}

