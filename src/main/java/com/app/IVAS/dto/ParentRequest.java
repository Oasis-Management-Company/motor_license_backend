package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentRequest {
    List<ChildRequest> childRequests;
    private String parentDescription;
    private Double totalAmount;
    private String firstName;
    private String lastName;
    private String email;
    private String transactionId;
    private String custReference;
}
