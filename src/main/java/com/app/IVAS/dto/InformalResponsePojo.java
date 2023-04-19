package com.app.IVAS.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class InformalResponsePojo {
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorCode")
    private String errorCode;
    @JsonProperty("data")
    private List<PaymentHistoryDto> data;
    @JsonProperty("success")
    private String success;
    @JsonProperty("timestamp")
    private String timestamp;
}
