package com.app.IVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindByNinDataResponsePojo {

    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errors")
    private List<String> errors;
    @JsonProperty("data")
    private NIN responseData;


}
