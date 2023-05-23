package com.app.IVAS.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentRequest {
    private Double amount;
    private String referenceNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String custReference;
    private String description;
    private String dateBooked;
    private String dateExpired;
    private String itemCode;
    List<ChildRequest> extendedData;
    private String name;
}
