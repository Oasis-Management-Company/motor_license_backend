package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInvoiceOffenseType is a Querydsl query type for InvoiceOffenseType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvoiceOffenseType extends EntityPathBase<InvoiceOffenseType> {

    private static final long serialVersionUID = -1637420659L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvoiceOffenseType invoiceOffenseType = new QInvoiceOffenseType("invoiceOffenseType");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final DateTimePath<java.time.LocalDateTime> expiryDate = createDateTime("expiryDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInvoice invoice;

    public final QOffense offense;

    public final StringPath reference = createString("reference");

    public final EnumPath<com.app.IVAS.Enum.RegType> regType = createEnum("regType", com.app.IVAS.Enum.RegType.class);

    public final StringPath revenuecode = createString("revenuecode");

    public QInvoiceOffenseType(String variable) {
        this(InvoiceOffenseType.class, forVariable(variable), INITS);
    }

    public QInvoiceOffenseType(Path<? extends InvoiceOffenseType> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvoiceOffenseType(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvoiceOffenseType(PathMetadata metadata, PathInits inits) {
        this(InvoiceOffenseType.class, metadata, inits);
    }

    public QInvoiceOffenseType(Class<? extends InvoiceOffenseType> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invoice = inits.isInitialized("invoice") ? new QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
        this.offense = inits.isInitialized("offense") ? new QOffense(forProperty("offense"), inits.get("offense")) : null;
    }

}

