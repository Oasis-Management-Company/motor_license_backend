package com.app.IVAS.entity.userManagement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.app.IVAS.Enum.RegType;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPortalUser is a Querydsl query type for PortalUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPortalUser extends EntityPathBase<PortalUser> {

    private static final long serialVersionUID = 291681121L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPortalUser portalUser = new QPortalUser("portalUser");

    public final QStatusEntity _super;

    public final StringPath address = createString("address");

    public final QArea area;

    public final StringPath asin = createString("asin");

    public final EnumPath<RegType> regType = createEnum("regType", RegType.class);


    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    public final DateTimePath<java.util.Date> dateOfBirth = createDateTime("dateOfBirth", java.util.Date.class);

    // inherited
    public final QPortalUser deactivatedBy;

    public final StringPath displayName = createString("displayName");

    public final StringPath email = createString("email");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final StringPath firstName = createString("firstName");

    public final StringPath generatedPassword = createString("generatedPassword");

    //inherited
    public final NumberPath<Long> id;

    public final StringPath image = createString("image");

    public final StringPath lastName = createString("lastName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final QPortalUser lastUpdatedBy;

    public final com.app.IVAS.entity.userManagement.QLga lga;

    public final StringPath nationalIdentificationNumber = createString("nationalIdentificationNumber");

    public final com.app.IVAS.entity.userManagement.QZonalOffice office;

    public final StringPath otherNames = createString("otherNames");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath preferredName = createString("preferredName");
    public final QRole role;
    public final NumberPath<Long> stateId = createNumber("stateId", Long.class);

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;
    public final StringPath username = createString("username");

    public final BooleanPath userVerified = createBoolean("userVerified");

    public QPortalUser(String variable) {
        this(PortalUser.class, forVariable(variable), INITS);
    }

    public QPortalUser(Path<? extends PortalUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPortalUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPortalUser(PathMetadata metadata, PathInits inits) {
        this(PortalUser.class, metadata, inits);
    }

    public QPortalUser(Class<? extends PortalUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QStatusEntity(type, metadata, inits);
        this.area = inits.isInitialized("area") ? new QArea(forProperty("area"), inits.get("area")) : null;
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.lga = inits.isInitialized("lga") ? new QLga(forProperty("lga")) : null;
        this.office = inits.isInitialized("office") ? new QZonalOffice(forProperty("office"), inits.get("office")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
        this.status = _super.status;
    }

}

