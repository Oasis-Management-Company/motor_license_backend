package com.app.IVAS.entity.userManagement;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QLga is a Querydsl query type for Lga
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLga extends EntityPathBase<Lga> {

    private static final long serialVersionUID = -186091396L;

    public static final QLga lga = new QLga("lga");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> stateId = createNumber("stateId", Long.class);

    public final StringPath status = createString("status");

    public QLga(String variable) {
        super(Lga.class, forVariable(variable));
    }

    public QLga(Path<? extends Lga> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLga(PathMetadata metadata) {
        super(Lga.class, metadata);
    }

}

