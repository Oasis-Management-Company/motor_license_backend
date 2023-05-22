package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentRequest {
    List<ChildRequest> extendedData;
    private String parentDescription;
    private Double totalAmount;
    private String firstName;
    private String lastName;
    private String email;
    private String transactionId;
    private String custReference;
    private String referenceNumber;
    private String dateBooked;
    private String dateExpired;
    private String itemCode;
    private Double amount;
    private String name;
    private String description;
}
