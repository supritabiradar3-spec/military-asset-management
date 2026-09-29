package com.military.assetmanagement.exception;

import com.military.assetmanagement.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            DisabledException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        log.warn("Authentication failed on {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Invalid username or password")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientInventoryException(
            InsufficientInventoryException ex,
            HttpServletRequest request
    ) {
        log.warn("Insufficient inventory on {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Insufficient Inventory")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOperationException(
            InvalidOperationException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid operation on {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Operation")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(ex.getMessage() != null && !ex.getMessage().isEmpty() && !ex.getMessage().equals("Access Denied")
                        ? ex.getMessage()
                        : "Access denied: You do not have sufficient permissions to access this resource")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed")
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception occurred on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
