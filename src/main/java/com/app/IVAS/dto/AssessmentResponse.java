package com.app.IVAS.dto;

import lombok.Data;

@Data
public class AssessmentResponse {

    private String statusCode;
    private String message;
    private String paymentReference;
    private String description;



}
