package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOffense is a Querydsl query type for Offense
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOffense extends EntityPathBase<Offense> {

    private static final long serialVersionUID = -1321089420L;

    public static final QOffense offense = new QOffense("offense");

    public final StringPath action = createString("action");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QOffense(String variable) {
        super(Offense.class, forVariable(variable));
    }

    public QOffense(Path<? extends Offense> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOffense(PathMetadata metadata) {
        super(Offense.class, metadata);
    }

}

