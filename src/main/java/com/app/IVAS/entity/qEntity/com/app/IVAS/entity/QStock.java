package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStock is a Querydsl query type for Stock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStock extends EntityPathBase<Stock> {

    private static final long serialVersionUID = 695481696L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStock stock = new QStock("stock");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    public final StringPath endCode = createString("endCode");

    public final NumberPath<Long> endRange = createNumber("endRange", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final QPrefix startCode;

    public final NumberPath<Long> startRange = createNumber("startRange", Long.class);

    public final QPlateNumberSubType subType;

    public final QPlateNumberType type;

    public QStock(String variable) {
        this(Stock.class, forVariable(variable), INITS);
    }

    public QStock(Path<? extends Stock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStock(PathMetadata metadata, PathInits inits) {
        this(Stock.class, metadata, inits);
    }

    public QStock(Class<? extends Stock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.startCode = inits.isInitialized("startCode") ? new QPrefix(forProperty("startCode")) : null;
        this.subType = inits.isInitialized("subType") ? new QPlateNumberSubType(forProperty("subType"), inits.get("subType")) : null;
        this.type = inits.isInitialized("type") ? new QPlateNumberType(forProperty("type")) : null;
    }

}

