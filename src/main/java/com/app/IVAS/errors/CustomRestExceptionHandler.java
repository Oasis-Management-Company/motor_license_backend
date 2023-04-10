package com.app.IVAS.errors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Map;

@EnableWebMvc
@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public Map<String, String> handleValidationExceptions(Map<String, String> errors) {

        return errors;
    }

//    @ExceptionHandler(CustomBadRequestException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), false, error, null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


//    @ExceptionHandler({ ConstraintViolationException.class })
//    public ResponseEntity<Object> handleConstraintViolation(
//            ConstraintViolationException ex, WebRequest request) {
//        List<String> errors = new ArrayList<String>();
//        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            errors.add(violation.getRootBeanClass().getName() + " " +
//                    violation.getPropertyPath() + ": " + violation.getMessage());
//        }
//
//        final ApiError apiError =
//                new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), false, errors, null);
//        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//    }

    @Override
//    @ExceptionHandler(CustomBadRequestException.class)
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), false, new ArrayList<>(), null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
