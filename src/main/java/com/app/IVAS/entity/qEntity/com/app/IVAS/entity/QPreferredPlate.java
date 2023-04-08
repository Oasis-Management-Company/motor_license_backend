package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPreferredPlate is a Querydsl query type for PreferredPlate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPreferredPlate extends EntityPathBase<PreferredPlate> {

    private static final long serialVersionUID = 356170763L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPreferredPlate preferredPlate = new QPreferredPlate("preferredPlate");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> numberOfPlates = createNumber("numberOfPlates", Long.class);

    public final QPlateNumberRequest request;

    public QPreferredPlate(String variable) {
        this(PreferredPlate.class, forVariable(variable), INITS);
    }

    public QPreferredPlate(Path<? extends PreferredPlate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPreferredPlate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPreferredPlate(PathMetadata metadata, PathInits inits) {
        this(PreferredPlate.class, metadata, inits);
    }

    public QPreferredPlate(Class<? extends PreferredPlate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.request = inits.isInitialized("request") ? new QPlateNumberRequest(forProperty("request"), inits.get("request")) : null;
    }

}

