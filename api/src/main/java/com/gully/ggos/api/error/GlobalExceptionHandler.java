package com.gully.ggos.api.error;

import com.gully.ggos.api.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", request, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String code = switch (status) {
            case UNAUTHORIZED -> "AUTH_EXPIRED";
            case FORBIDDEN -> "FORBIDDEN";
            case NOT_FOUND -> "NOT_FOUND";
            default -> "UPSTREAM_ERROR";
        };
        return buildError(status, code, ex.getReason() != null ? ex.getReason() : "Request failed", request, null);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitException ex, HttpServletRequest request) {
        return buildError(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMITED", ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error", request, null);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            Object details) {
        String requestId = (String) request.getAttribute("requestId");
        ApiResponse.ApiError error = ApiResponse.ApiError.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .requestId(requestId)
                .details(details)
                .build();
        return ResponseEntity.status(status).body(ApiResponse.error(error));
    }
}
