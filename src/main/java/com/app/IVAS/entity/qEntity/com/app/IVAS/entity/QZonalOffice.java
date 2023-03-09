package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QZonalOffice is a Querydsl query type for ZonalOffice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QZonalOffice extends EntityPathBase<ZonalOffice> {

    private static final long serialVersionUID = 1834743914L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QZonalOffice zonalOffice = new QZonalOffice("zonalOffice");

    public final StringPath address = createString("address");

    public final StringPath contact = createString("contact");

    public final StringPath head = createString("head");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QZone zone;

    public QZonalOffice(String variable) {
        this(ZonalOffice.class, forVariable(variable), INITS);
    }

    public QZonalOffice(Path<? extends ZonalOffice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QZonalOffice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QZonalOffice(PathMetadata metadata, PathInits inits) {
        this(ZonalOffice.class, metadata, inits);
    }

    public QZonalOffice(Class<? extends ZonalOffice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.zone = inits.isInitialized("zone") ? new QZone(forProperty("zone"), inits.get("zone")) : null;
    }

}

