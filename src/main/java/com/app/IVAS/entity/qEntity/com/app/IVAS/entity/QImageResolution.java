package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QImageResolution is a Querydsl query type for ImageResolution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImageResolution extends EntityPathBase<ImageResolution> {

    private static final long serialVersionUID = 357151281L;

    public static final QImageResolution imageResolution = new QImageResolution("imageResolution");

    public final StringPath code = createString("code");

    public final EnumPath<com.app.IVAS.Enum.DataStorageConstant> dataStorageConstant = createEnum("dataStorageConstant", com.app.IVAS.Enum.DataStorageConstant.class);

    public final DateTimePath<java.sql.Timestamp> dateCreated = createDateTime("dateCreated", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> dateUpdated = createDateTime("dateUpdated", java.sql.Timestamp.class);

    public final StringPath fileCode = createString("fileCode");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final BooleanPath original = createBoolean("original");

    public final StringPath url = createString("url");

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public QImageResolution(String variable) {
        super(ImageResolution.class, forVariable(variable));
    }

    public QImageResolution(Path<? extends ImageResolution> path) {
        super(path.getType(), path.getMetadata());
    }

    public QImageResolution(PathMetadata metadata) {
        super(ImageResolution.class, metadata);
    }

}

