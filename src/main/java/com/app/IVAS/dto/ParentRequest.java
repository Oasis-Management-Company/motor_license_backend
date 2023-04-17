package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentRequest {
    List<ChildRequest> childRequests;
    private String parentDescription;
    private Double TotalAmount;
    private String FirstName;
    private String LastName;
    private String email;
    private String TransactionId;
    private String CustReference;
}
