package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QNIN is a Querydsl query type for NIN
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNIN extends EntityPathBase<NIN> {

    private static final long serialVersionUID = -1210454307L;

    public static final QNIN nIN = new QNIN("nIN");

    public final StringPath asin = createString("asin");

    public final StringPath birthCountry = createString("birthCountry");

    public final StringPath birthDate = createString("birthDate");

    public final StringPath birthLGA = createString("birthLGA");

    public final StringPath birthState = createString("birthState");

    public final NumberPath<Long> centralId = createNumber("centralId", Long.class);

    public final DateTimePath<java.util.Date> dateCreated = createDateTime("dateCreated", java.util.Date.class);

    public final DateTimePath<java.util.Date> dateUpdated = createDateTime("dateUpdated", java.util.Date.class);

    public final StringPath dob = createString("dob");

    public final StringPath educationalLevel = createString("educationalLevel");

    public final StringPath email = createString("email");

    public final StringPath employmentStatus = createString("employmentStatus");

    public final StringPath firstName = createString("firstName");

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> height = createNumber("height", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath maritalStatus = createString("maritalStatus");

    public final StringPath middleName = createString("middleName");

    public final StringPath nin = createString("nin");

    public final StringPath nokAddress = createString("nokAddress");

    public final StringPath nokAddress2 = createString("nokAddress2");

    public final StringPath nokFirstName = createString("nokFirstName");

    public final StringPath nokLGA = createString("nokLGA");

    public final StringPath nokState = createString("nokState");

    public final StringPath nokSurname = createString("nokSurname");

    public final StringPath nokTown = createString("nokTown");

    public final StringPath nspokenLang = createString("nspokenLang");

    public final StringPath photo = createString("photo");

    public final StringPath photoFileCode = createString("photoFileCode");

    public final StringPath profession = createString("profession");

    public final StringPath religion = createString("religion");

    public final StringPath residenceAddressLine1 = createString("residenceAddressLine1");

    public final StringPath residenceLga = createString("residenceLga");

    public final StringPath residenceState = createString("residenceState");

    public final StringPath residenceStatus = createString("residenceStatus");

    public final StringPath residenceTown = createString("residenceTown");

    public final StringPath selfOriginLga = createString("selfOriginLga");

    public final StringPath selfOriginPlace = createString("selfOriginPlace");

    public final StringPath selfOriginState = createString("selfOriginState");

    public final StringPath signature = createString("signature");

    public final StringPath signatureFileCode = createString("signatureFileCode");

    public final StringPath surname = createString("surname");

    public final StringPath telePhoneNumber = createString("telePhoneNumber");

    public final StringPath title = createString("title");

    public QNIN(String variable) {
        super(NIN.class, forVariable(variable));
    }

    public QNIN(Path<? extends NIN> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNIN(PathMetadata metadata) {
        super(NIN.class, metadata);
    }

}

