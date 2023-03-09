package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPlateNumberType is a Querydsl query type for PlateNumberType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumberType extends EntityPathBase<PlateNumberType> {

    private static final long serialVersionUID = 1416613795L;

    public static final QPlateNumberType plateNumberType = new QPlateNumberType("plateNumberType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QPlateNumberType(String variable) {
        super(PlateNumberType.class, forVariable(variable));
    }

    public QPlateNumberType(Path<? extends PlateNumberType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlateNumberType(PathMetadata metadata) {
        super(PlateNumberType.class, metadata);
    }

}

