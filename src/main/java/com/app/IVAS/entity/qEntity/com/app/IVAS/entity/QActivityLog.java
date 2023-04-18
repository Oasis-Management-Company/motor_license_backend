package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QActivityLog is a Querydsl query type for ActivityLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QActivityLog extends EntityPathBase<ActivityLog> {

    private static final long serialVersionUID = 1717766271L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QActivityLog activityLog = new QActivityLog("activityLog");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final EnumPath<com.app.IVAS.Enum.ActivityStatusConstant> action = createEnum("action", com.app.IVAS.Enum.ActivityStatusConstant.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final StringPath description = createString("description");

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public QActivityLog(String variable) {
        this(ActivityLog.class, forVariable(variable), INITS);
    }

    public QActivityLog(Path<? extends ActivityLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QActivityLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QActivityLog(PathMetadata metadata, PathInits inits) {
        this(ActivityLog.class, metadata, inits);
    }

    public QActivityLog(Class<? extends ActivityLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.status = _super.status;
    }

}

