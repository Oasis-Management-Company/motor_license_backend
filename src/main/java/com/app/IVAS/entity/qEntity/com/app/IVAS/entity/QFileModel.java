package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFileModel is a Querydsl query type for FileModel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileModel extends EntityPathBase<FileModel> {

    private static final long serialVersionUID = -1531433897L;

    public static final QFileModel fileModel = new QFileModel("fileModel");

    public final StringPath code = createString("code");

    public final EnumPath<com.app.IVAS.Enum.DataStorageConstant> dataStorageConstant = createEnum("dataStorageConstant", com.app.IVAS.Enum.DataStorageConstant.class);

    public final DateTimePath<java.sql.Timestamp> dateCreated = createDateTime("dateCreated", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> dateUpdated = createDateTime("dateUpdated", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ImageResolution, QImageResolution> imageResolutions = this.<ImageResolution, QImageResolution>createList("imageResolutions", ImageResolution.class, QImageResolution.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath url = createString("url");

    public QFileModel(String variable) {
        super(FileModel.class, forVariable(variable));
    }

    public QFileModel(Path<? extends FileModel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileModel(PathMetadata metadata) {
        super(FileModel.class, metadata);
    }

}

