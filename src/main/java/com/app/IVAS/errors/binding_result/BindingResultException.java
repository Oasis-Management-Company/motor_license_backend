package com.app.IVAS.errors.binding_result;

import com.app.IVAS.errors.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BindingResultException {

    private final Logger logger = Logger.getLogger(BindingResultException.class.getName());

    public ResponseEntity<Object> bindingErrorException(BindingResult bindingResult) {

        ApiError apiError = null;
        List<String> errors = new ArrayList<>();

        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();

            logger.info(fieldName);

            String errorMessage = error.getDefaultMessage();
            String fullErrorMessage = fieldName + " " + errorMessage;

            logger.info(fullErrorMessage);

            errors.add(fullErrorMessage);
        });

        apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Kindly input the required fields", false, errors, null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
