package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlateNumberSubType is a Querydsl query type for PlateNumberSubType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlateNumberSubType extends EntityPathBase<PlateNumberSubType> {

    private static final long serialVersionUID = -1022419823L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlateNumberSubType plateNumberSubType = new QPlateNumberSubType("plateNumberSubType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QPlateNumberType type;

    public QPlateNumberSubType(String variable) {
        this(PlateNumberSubType.class, forVariable(variable), INITS);
    }

    public QPlateNumberSubType(Path<? extends PlateNumberSubType> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlateNumberSubType(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlateNumberSubType(PathMetadata metadata, PathInits inits) {
        this(PlateNumberSubType.class, metadata, inits);
    }

    public QPlateNumberSubType(Class<? extends PlateNumberSubType> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.type = inits.isInitialized("type") ? new QPlateNumberType(forProperty("type")) : null;
    }

}

