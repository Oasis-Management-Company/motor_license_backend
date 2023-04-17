package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOffense is a Querydsl query type for Offense
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOffense extends EntityPathBase<Offense> {

    private static final long serialVersionUID = -1321089420L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOffense offense = new QOffense("offense");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final StringPath actionTaken = createString("actionTaken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final NumberPath<Double> fine = createNumber("fine", Double.class);

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final StringPath name = createString("name");

    public final StringPath offenceCode = createString("offenceCode");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final StringPath revenueCode = createString("revenueCode");

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public QOffense(String variable) {
        this(Offense.class, forVariable(variable), INITS);
    }

    public QOffense(Path<? extends Offense> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOffense(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOffense(PathMetadata metadata, PathInits inits) {
        this(Offense.class, metadata, inits);
    }

    public QOffense(Class<? extends Offense> type, PathMetadata metadata, PathInits inits) {
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

