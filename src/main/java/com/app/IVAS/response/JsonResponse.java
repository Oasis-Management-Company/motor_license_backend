package com.app.IVAS.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.Date;


@Data
public class JsonResponse implements Serializable {

    @JsonProperty("status")
    private HttpStatus status = HttpStatus.OK;
    
    @JsonProperty("success")
    private boolean success = true;
    
    @JsonProperty("errorCode")
    private int errorCode = 0;
        
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("message")
    private String message = "";
    
    @JsonIgnore
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp = new Date();
    
    
    public JsonResponse(HttpStatus status, boolean error, String message, Object data) {
        this.status = status;
        this.success = !status.isError();
        this.message = message;
        this.data = data;
    }

    public JsonResponse(HttpStatus status, String message, Object data, Date timestamp) {
        this.status = status;
        this.success = !status.isError();
        this.timestamp = timestamp;
        this.message = message;
        this.data = data;
    }

    public JsonResponse(HttpStatus status, String message, Object data) {
        this.status = status;
        this.success = !status.isError();
        this.message = message;
        this.data = data;
    }
    
    public JsonResponse(HttpStatus status, String message) {
        this.status = status;
        this.success = !status.isError();
        this.message = message;
    }
    
    public JsonResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public JsonResponse(String message) {
        this.message = message;
    }
        
    public JsonResponse(Object data) {
        this.data = data;
    }
    
    
    /**
     * @param result
     * @return
     */
    public static String[] getErrors(BindingResult result) {
        String[] errors = new String[result.getErrorCount()];
        int i = 0;
        for (Object object : result.getAllErrors()) {
            FieldError fieldError = (FieldError) object;
            errors[i] = fieldError.getDefaultMessage();
            i++;
        }
        return errors;
    }
    
}
