package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrefix is a Querydsl query type for Prefix
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrefix extends EntityPathBase<Prefix> {

    private static final long serialVersionUID = -2933368L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrefix prefix = new QPrefix("prefix");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QPlateNumberSubType subType;

    public QPrefix(String variable) {
        this(Prefix.class, forVariable(variable), INITS);
    }

    public QPrefix(Path<? extends Prefix> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrefix(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrefix(PathMetadata metadata, PathInits inits) {
        this(Prefix.class, metadata, inits);
    }

    public QPrefix(Class<? extends Prefix> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.subType = inits.isInitialized("subType") ? new QPlateNumberSubType(forProperty("subType"), inits.get("subType")) : null;
    }

}

