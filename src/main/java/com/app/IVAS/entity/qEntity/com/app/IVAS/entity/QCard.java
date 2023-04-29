package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCard is a Querydsl query type for Card
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCard extends EntityPathBase<Card> {

    private static final long serialVersionUID = 1130318726L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCard card = new QCard("card");

    public final com.app.IVAS.entity.userManagement.QStatusEntity _super;

    public final EnumPath<com.app.IVAS.Enum.CardStatusConstant> cardStatus = createEnum("cardStatus", com.app.IVAS.Enum.CardStatusConstant.class);

    public final EnumPath<com.app.IVAS.Enum.CardTypeConstant> cardType = createEnum("cardType", com.app.IVAS.Enum.CardTypeConstant.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateDeactivated;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser deactivatedBy;

    public final DateTimePath<java.time.LocalDateTime> expiryDate = createDateTime("expiryDate", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id;

    public final QInvoice invoice;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedAt;

    // inherited
    public final com.app.IVAS.entity.userManagement.QPortalUser lastUpdatedBy;

    public final EnumPath<com.app.IVAS.Enum.RegType> regType = createEnum("regType", com.app.IVAS.Enum.RegType.class);

    //inherited
    public final EnumPath<com.app.IVAS.Enum.GenericStatusConstant> status;

    public final QVehicle vehicle;

    public QCard(String variable) {
        this(Card.class, forVariable(variable), INITS);
    }

    public QCard(Path<? extends Card> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCard(PathMetadata metadata, PathInits inits) {
        this(Card.class, metadata, inits);
    }

    public QCard(Class<? extends Card> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new com.app.IVAS.entity.userManagement.QStatusEntity(type, metadata, inits);
        this.createdAt = _super.createdAt;
        this.createdBy = _super.createdBy;
        this.dateDeactivated = _super.dateDeactivated;
        this.deactivatedBy = _super.deactivatedBy;
        this.id = _super.id;
        this.invoice = inits.isInitialized("invoice") ? new QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
        this.lastUpdatedAt = _super.lastUpdatedAt;
        this.lastUpdatedBy = _super.lastUpdatedBy;
        this.status = _super.status;
        this.vehicle = inits.isInitialized("vehicle") ? new QVehicle(forProperty("vehicle"), inits.get("vehicle")) : null;
    }

}

