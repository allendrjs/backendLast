package org.rocs.osdrmsa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes the same JSON error shape as GlobalExceptionHandler
 * ({timestamp, status, code, message}), for use by security components that
 * run inside the filter chain - before DispatcherServlet - where
 * @RestControllerAdvice can't reach them.
 */
final class SecurityErrorResponseWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SecurityErrorResponseWriter() {
    }

    static void write(HttpServletResponse response, HttpStatus status, String code, String message)
            throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);

        OBJECT_MAPPER.writeValue(response.getWriter(), body);
    }
}
