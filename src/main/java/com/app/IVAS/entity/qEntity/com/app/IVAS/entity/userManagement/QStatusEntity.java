package com.app.IVAS.entity.userManagement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStatusEntity is a Querydsl query type for StatusEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QStatusEntity extends EntityPathBase<StatusEntity> {

    private static final long serialVersionUID = 2122655999L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStatusEntity statusEntity = new QStatusEntity("statusEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QPortalUser createdBy;

    public final DateTimePath<java.time.LocalDateTime> dateDeactivated = createDateTime("dateDeactivated", java.time.LocalDateTime.class);

    public final QPortalUser deactivatedBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt = createDateTime("lastUpdatedAt", java.time.LocalDateTime.class);

    public final QPortalUser lastUpdatedBy;

    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status = createEnum("status", com.app.IVAS.Enum.GenericStatusConstant.class);

    public QStatusEntity(String variable) {
        this(StatusEntity.class, forVariable(variable), INITS);
    }

    public QStatusEntity(Path<? extends StatusEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStatusEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStatusEntity(PathMetadata metadata, PathInits inits) {
        this(StatusEntity.class, metadata, inits);
    }

    public QStatusEntity(Class<? extends StatusEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new QPortalUser(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.deactivatedBy = inits.isInitialized("deactivatedBy") ? new QPortalUser(forProperty("deactivatedBy"), inits.get("deactivatedBy")) : null;
        this.lastUpdatedBy = inits.isInitialized("lastUpdatedBy") ? new QPortalUser(forProperty("lastUpdatedBy"), inits.get("lastUpdatedBy")) : null;
    }

}

