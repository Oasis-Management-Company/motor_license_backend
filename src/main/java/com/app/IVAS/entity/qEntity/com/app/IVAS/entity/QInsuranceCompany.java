package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInsuranceCompany is a Querydsl query type for InsuranceCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInsuranceCompany extends EntityPathBase<InsuranceCompany> {

    private static final long serialVersionUID = -1208192071L;

    public static final QInsuranceCompany insuranceCompany = new QInsuranceCompany("insuranceCompany");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QInsuranceCompany(String variable) {
        super(InsuranceCompany.class, forVariable(variable));
    }

    public QInsuranceCompany(Path<? extends InsuranceCompany> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInsuranceCompany(PathMetadata metadata) {
        super(InsuranceCompany.class, metadata);
    }

}

