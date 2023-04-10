package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserDemographicIndividual is a Querydsl query type for UserDemographicIndividual
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDemographicIndividual extends EntityPathBase<UserDemographicIndividual> {

    private static final long serialVersionUID = 1917995613L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserDemographicIndividual userDemographicIndividual = new QUserDemographicIndividual("userDemographicIndividual");

    public final StringPath address = createString("address");

    public final NumberPath<Long> area = createNumber("area", Long.class);

    public final StringPath asin = createString("asin");

    public final StringPath bvn = createString("bvn");

    public final StringPath city = createString("city");

    public final StringPath dateCreated = createString("dateCreated");

    public final StringPath email = createString("email");

    public final StringPath firstname = createString("firstname");

    public final StringPath houseNumber = createString("houseNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath landMark = createString("landMark");

    public final StringPath lastname = createString("lastname");

    public final BooleanPath legacy = createBoolean("legacy");

    public final NumberPath<Long> lga = createNumber("lga", Long.class);

    public final NumberPath<Long> lgaId = createNumber("lgaId", Long.class);

    public final StringPath middlename = createString("middlename");

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath photo = createString("photo");

    public final NumberPath<Long> stateId = createNumber("stateId", Long.class);

    public final com.app.IVAS.entity.userManagement.QPortalUser user;

    public QUserDemographicIndividual(String variable) {
        this(UserDemographicIndividual.class, forVariable(variable), INITS);
    }

    public QUserDemographicIndividual(Path<? extends UserDemographicIndividual> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserDemographicIndividual(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserDemographicIndividual(PathMetadata metadata, PathInits inits) {
        this(UserDemographicIndividual.class, metadata, inits);
    }

    public QUserDemographicIndividual(Class<? extends UserDemographicIndividual> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.app.IVAS.entity.userManagement.QPortalUser(forProperty("user"), inits.get("user")) : null;
    }

}

