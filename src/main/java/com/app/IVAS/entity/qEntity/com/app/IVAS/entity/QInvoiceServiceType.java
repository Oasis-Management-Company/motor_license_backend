package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInvoiceServiceType is a Querydsl query type for InvoiceServiceType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvoiceServiceType extends EntityPathBase<InvoiceServiceType> {

    private static final long serialVersionUID = -1291007976L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvoiceServiceType invoiceServiceType = new QInvoiceServiceType("invoiceServiceType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInvoice invoice;

    public final QServiceType serviceType;

    public QInvoiceServiceType(String variable) {
        this(InvoiceServiceType.class, forVariable(variable), INITS);
    }

    public QInvoiceServiceType(Path<? extends InvoiceServiceType> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvoiceServiceType(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvoiceServiceType(PathMetadata metadata, PathInits inits) {
        this(InvoiceServiceType.class, metadata, inits);
    }

    public QInvoiceServiceType(Class<? extends InvoiceServiceType> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invoice = inits.isInitialized("invoice") ? new QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
        this.serviceType = inits.isInitialized("serviceType") ? new QServiceType(forProperty("serviceType"), inits.get("serviceType")) : null;
    }

}

