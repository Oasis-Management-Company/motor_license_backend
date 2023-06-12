package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEditPortalUser is a Querydsl query type for EditPortalUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEditPortalUser extends EntityPathBase<EditPortalUser> {

    private static final long serialVersionUID = -404960009L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEditPortalUser editPortalUser = new QEditPortalUser("editPortalUser");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    public final StringPath dateCreated = createString("dateCreated");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final StringPath displayName = createString("displayName");

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    //inherited
    public final NumberPath<Long> id;

    public final StringPath lastName = createString("lastName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final StringPath otherNames = createString("otherNames");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath parentPhoneNumber = createString("parentPhoneNumber");

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public QEditPortalUser(String variable) {
        this(EditPortalUser.class, forVariable(variable), INITS);
    }

    public QEditPortalUser(Path<? extends EditPortalUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEditPortalUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEditPortalUser(PathMetadata metadata, PathInits inits) {
        this(EditPortalUser.class, metadata, inits);
    }

    public QEditPortalUser(Class<? extends EditPortalUser> type, PathMetadata metadata, PathInits inits) {
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

