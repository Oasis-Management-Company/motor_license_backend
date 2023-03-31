package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPrefix is a Querydsl query type for Prefix
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrefix extends EntityPathBase<Prefix> {

    private static final long serialVersionUID = -2933368L;

    public static final QPrefix prefix = new QPrefix("prefix");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QPrefix(String variable) {
        super(Prefix.class, forVariable(variable));
    }

    public QPrefix(Path<? extends Prefix> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPrefix(PathMetadata metadata) {
        super(Prefix.class, metadata);
    }

}

