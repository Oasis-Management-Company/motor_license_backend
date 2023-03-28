package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkFLowLog is a Querydsl query type for WorkFLowLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkFLowLog extends EntityPathBase<WorkFLowLog> {

    private static final long serialVersionUID = 801515983L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkFLowLog workFLowLog = new QWorkFLowLog("workFLowLog");

    public final StringPath action = createString("action");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPlateNumberRequest request;

    public QWorkFLowLog(String variable) {
        this(WorkFLowLog.class, forVariable(variable), INITS);
    }

    public QWorkFLowLog(Path<? extends WorkFLowLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkFLowLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkFLowLog(PathMetadata metadata, PathInits inits) {
        this(WorkFLowLog.class, metadata, inits);
    }

    public QWorkFLowLog(Class<? extends WorkFLowLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.request = inits.isInitialized("request") ? new QPlateNumberRequest(forProperty("request"), inits.get("request")) : null;
    }

}

