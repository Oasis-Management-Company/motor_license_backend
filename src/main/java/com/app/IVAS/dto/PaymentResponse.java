package com.app.IVAS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private String itemName;
    private String itemAmount;
    private String paymentLogId;
    private String custReference;
    private String amount;
    private String paymentMethod;
    private String paymentReference;
    private String terminalId;
    private String channelName;
    private String location;
    private String paymentDate;
    private String institutionId;
    private String institutionName;
    private String branchName;
    private String bankName;
    private String customerName;
    private String otherCustomerInfo;
    private String receiptNo;
    private String collectionsAccount;
    private String bankCode;
    private String customerAddress;
    private String customerPhoneNumber;
    private String depositorName;
    private String depositSlipNumber;
    private String paymentCurrency;
    private String paymentStatus;
    private String isReversal;
    private String settlementDate;
    private String feeName;
    private String thirdPartyCode;
    private String originalPaymentReference;
    private String teller;
}
