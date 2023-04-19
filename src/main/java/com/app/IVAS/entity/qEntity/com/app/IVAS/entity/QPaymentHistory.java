package com.app.IVAS.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentHistory is a Querydsl query type for PaymentHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentHistory extends EntityPathBase<PaymentHistory> {

    private static final long serialVersionUID = 1975405732L;

    public static final QPaymentHistory paymentHistory = new QPaymentHistory("paymentHistory");

    public final StringPath amount = createString("amount");

    public final StringPath bankCode = createString("bankCode");

    public final StringPath bankName = createString("bankName");

    public final StringPath branchName = createString("branchName");

    public final StringPath channelName = createString("channelName");

    public final StringPath collectionsAccount = createString("collectionsAccount");

    public final StringPath customerAddress = createString("customerAddress");

    public final StringPath customerName = createString("customerName");

    public final StringPath customerPhoneNumber = createString("customerPhoneNumber");

    public final StringPath custReference = createString("custReference");

    public final StringPath depositorName = createString("depositorName");

    public final StringPath depositSlipNumber = createString("depositSlipNumber");

    public final StringPath feeName = createString("feeName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath institutionId = createString("institutionId");

    public final StringPath institutionName = createString("institutionName");

    public final StringPath isReversal = createString("isReversal");

    public final StringPath itemAmount = createString("itemAmount");

    public final StringPath itemName = createString("itemName");

    public final StringPath location = createString("location");

    public final StringPath log = createString("log");

    public final StringPath originalPaymentReference = createString("originalPaymentReference");

    public final StringPath otherCustomerInfo = createString("otherCustomerInfo");

    public final StringPath paymentCurrency = createString("paymentCurrency");

    public final StringPath paymentDate = createString("paymentDate");

    public final StringPath paymentLogId = createString("paymentLogId");

    public final StringPath paymentMethod = createString("paymentMethod");

    public final StringPath paymentReference = createString("paymentReference");

    public final StringPath paymentStatus = createString("paymentStatus");

    public final StringPath receiptNo = createString("receiptNo");

    public final StringPath settlementDate = createString("settlementDate");

    public final StringPath teller = createString("teller");

    public final StringPath terminalId = createString("terminalId");

    public final StringPath thirdPartyCode = createString("thirdPartyCode");

    public QPaymentHistory(String variable) {
        super(PaymentHistory.class, forVariable(variable));
    }

    public QPaymentHistory(Path<? extends PaymentHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentHistory(PathMetadata metadata) {
        super(PaymentHistory.class, metadata);
    }

}

