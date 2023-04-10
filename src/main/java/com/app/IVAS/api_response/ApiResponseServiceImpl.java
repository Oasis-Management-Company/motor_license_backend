package com.app.IVAS.api_response;

import com.app.IVAS.Utils.MessageUtil;
import com.app.IVAS.errors.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ApiResponseServiceImpl {

    private final MessageUtil messageUtil;

    public ApiResponseServiceImpl(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    public ResponseEntity<?> internalServerError() {

        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageUtil.getMessage("internal.server.error", "en"),
                false,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> unAuthorized() {

        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED,
                messageUtil.getMessage("user.unauthorized", "en"),
                false,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> photoNotFoundWithNIN() {

        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage("photo.not.attached.to.nin", "en"),
                false,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> objectNotFound(String message) {

        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                message,
                false,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> asinNotFound() {

        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND,
                messageUtil.getMessage("asin.not.found", "en"),
                false,
                new ArrayList<>(),
                null
                );
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> asinRequired() {

        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                messageUtil.getMessage("asin.required", "en"),
                false,
                new ArrayList<>(),
                null
        );
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> failedToRetrieveUserInformation() {

        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                messageUtil.getMessage("failed.to.retrieve.user.information", "en"),
                false,
                new ArrayList<>(),
                null
        );
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> failedToRetrieveUploadedFile() {

        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                messageUtil.getMessage("failed.to.retrieve.user.information", "en"),
                false,
                new ArrayList<>(),
                null
        );
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> emailAlreadyExists() {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage("email.already.exists", "en"),
                false,
                new ArrayList<>(),
                null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> emailInvalid() {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage("email.invalid", "en"),
                false,
                new ArrayList<>(),
                null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> phoneNumberInvalid() {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage("phone_number.invalid", "en"),
                false,
                new ArrayList<>(),
                null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> phoneNumberAlreadyExists() {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage("phone_number.already.exists", "en"),
                false,
                new ArrayList<>(),
                null);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> okayResponseWithCustomMessage(String message) {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.OK.value(),
                HttpStatus.OK,
                messageUtil.getMessage(message, "en"),
                true,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    public ResponseEntity<?> badRequestResponseWithCustomMessage(String message) {
        ApiError apiError = null;
        apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.OK,
                messageUtil.getMessage(message, "en"),
                false,
                new ArrayList<>(),
                null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
