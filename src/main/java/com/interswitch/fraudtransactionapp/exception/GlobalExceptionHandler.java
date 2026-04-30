package com.interswitch.fraudtransactionapp.exception;

import com.interswitch.fraudtransactionapp.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request.getDescription(false));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request.getDescription(false));
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthenticationServiceException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request.getDescription(false));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {

        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request.getDescription(false));
    }



    @ExceptionHandler({
            ResourceExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictExceptions(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request.getDescription(false));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, String path) {
        log.error("Unhandled exception occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
