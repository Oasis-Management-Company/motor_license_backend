package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInsuranceResponse is a Querydsl query type for InsuranceResponse
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInsuranceResponse extends EntityPathBase<InsuranceResponse> {

    private static final long serialVersionUID = 1460192389L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInsuranceResponse insuranceResponse = new QInsuranceResponse("insuranceResponse");

    public final StringPath certificateNumber = createString("certificateNumber");

    public final StringPath chassisNumber = createString("chassisNumber");

    public final StringPath commencementDate = createString("commencementDate");

    public final StringPath engineNumber = createString("engineNumber");

    public final StringPath expirationDate = createString("expirationDate");

    public final StringPath fullName = createString("fullName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInvoice invoice;

    public final StringPath niidResponse = createString("niidResponse");

    public final StringPath policyNumber = createString("policyNumber");

    public final StringPath registrationNumber = createString("registrationNumber");

    public final StringPath renewalDate = createString("renewalDate");

    public final StringPath reprintCode = createString("reprintCode");

    public final StringPath transId = createString("transId");

    public final QVehicle vehicle;

    public final StringPath vehicleMake = createString("vehicleMake");

    public final StringPath vehicleModel = createString("vehicleModel");

    public final StringPath yearOfMake = createString("yearOfMake");

    public QInsuranceResponse(String variable) {
        this(InsuranceResponse.class, forVariable(variable), INITS);
    }

    public QInsuranceResponse(Path<? extends InsuranceResponse> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInsuranceResponse(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInsuranceResponse(PathMetadata metadata, PathInits inits) {
        this(InsuranceResponse.class, metadata, inits);
    }

    public QInsuranceResponse(Class<? extends InsuranceResponse> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invoice = inits.isInitialized("invoice") ? new QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
        this.vehicle = inits.isInitialized("vehicle") ? new QVehicle(forProperty("vehicle"), inits.get("vehicle")) : null;
    }

}

