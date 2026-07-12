package com.incubyte.car_dealership_inventory_system.exception;

import com.incubyte.car_dealership_inventory_system.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;

/**
 * Centralized exception handler that converts all exceptions into consistent ApiErrorResponse JSON.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ---- 404 ---- */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(),
                        request.getRequestURI(), ex.getErrorCode()));
    }

    /* ---- 409 (CONFLICT) ---- */

    @ExceptionHandler(DuplicateVehicleException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateVehicle(DuplicateVehicleException ex,
                                                                    HttpServletRequest request) {
        log.warn("Duplicate vehicle: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(),
                        request.getRequestURI(), ex.getErrorCode()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                                     HttpServletRequest request) {
        log.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(),
                        request.getRequestURI(), ex.getErrorCode()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException ex,
                                                                     HttpServletRequest request) {
        log.warn("Insufficient stock: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(),
                        request.getRequestURI(), ex.getErrorCode()));
    }

    /* ---- 401 (UNAUTHORIZED) ---- */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                                  HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(HttpStatus.UNAUTHORIZED,
                        "Invalid email or password",
                        request.getRequestURI(), "INVALID_CREDENTIALS"));
    }

    /* ---- 403 (FORBIDDEN) ---- */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                                HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(HttpStatus.FORBIDDEN,
                        "You do not have permission to perform this action",
                        request.getRequestURI(), "ACCESS_DENIED"));
    }

    /* ---- 400 (BAD REQUEST) ---- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(err -> new ApiErrorResponse.FieldError(
                        err.getField(),
                        err.getDefaultMessage(),
                        err.getRejectedValue()))
                .toList();

        log.warn("Validation failed: {} errors at {}", fieldErrors.size(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.ofValidation(request.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingPart(MissingServletRequestPartException ex,
                                                               HttpServletRequest request) {
        log.warn("Missing request part '{}' at {}", ex.getRequestPartName(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "Required request part '" + ex.getRequestPartName() + "' is not present",
                        request.getRequestURI(), "MISSING_REQUEST_PART"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest request) {
        log.warn("Malformed request body at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "Malformed request body: " + ex.getMostSpecificCause().getMessage(),
                        request.getRequestURI(), "MALFORMED_REQUEST"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                HttpServletRequest request) {
        log.warn("Type mismatch at {}: parameter '{}' value '{}' could not be converted to {}",
                request.getRequestURI(), ex.getName(), ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "Invalid value for parameter '" + ex.getName() + "'",
                        request.getRequestURI(), "INVALID_PARAMETER"));
    }

    /* ---- 415 (UNSUPPORTED MEDIA TYPE) ---- */

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        log.warn("Unsupported media type at {}: {}", request.getRequestURI(), ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiErrorResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "Content type '" + ex.getContentType() + "' is not supported. Use application/json",
                        request.getRequestURI(), "UNSUPPORTED_MEDIA_TYPE"));
    }

    /* ---- 409 (CONFLICT — optimistic locking) ---- */

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {
        log.warn("Optimistic lock conflict at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT,
                        "This resource was modified by another request. Please retry.",
                        request.getRequestURI(), "OPTIMISTIC_LOCK_CONFLICT"));
    }

    /* ---- 500 (INTERNAL SERVER ERROR) — fallback ---- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex,
                                                       HttpServletRequest request) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later.",
                        request.getRequestURI(), "INTERNAL_SERVER_ERROR"));
    }
}
