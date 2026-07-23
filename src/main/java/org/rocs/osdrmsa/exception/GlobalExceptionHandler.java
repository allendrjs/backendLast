package org.rocs.osdrmsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Central place for mapping known business exceptions to consistent JSON
 * error responses instead of leaking stack traces or generic 500s.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException e) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e.getMessage());
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Object> handleAccountLocked(AccountLockedException e) {
        return build(HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED", e.getMessage());
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<Object> handleAccountInactive(AccountInactiveException e) {
        return build(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFound(NoSuchElementException e) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleConflict(IllegalStateException e) {
        return build(HttpStatus.CONFLICT, "CONFLICT", e.getMessage());
    }

    /**
     * Thrown by @PreAuthorize denials. Method-security AOP intercepts the
     * controller call inside DispatcherServlet, so this exception reaches
     * @RestControllerAdvice rather than Spring Security's own
     * ExceptionTranslationFilter - without this handler it would fall
     * through to the generic 500 below instead of a 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException e) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You do not have permission to perform this action.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception e) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred.");
    }

    private ResponseEntity<Object> build(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
