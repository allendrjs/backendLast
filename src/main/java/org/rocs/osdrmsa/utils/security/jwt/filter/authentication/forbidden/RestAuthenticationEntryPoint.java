package org.rocs.osdrmsa.utils.security.jwt.filter.authentication.forbidden;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rocs.osdrmsa.utils.security.jwt.filter.authentication.SecurityErrorResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles requests that reach the filter chain with no, invalid, or expired
 * JWT (i.e. authorizeHttpRequests().anyRequest().authenticated() fails).
 * This runs inside ExceptionTranslationFilter, ahead of DispatcherServlet,
 * so GlobalExceptionHandler's @RestControllerAdvice never sees it - without
 * this bean Spring Security falls back to a bare 403 with no body instead
 * of a proper 401 in our standard JSON shape.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        SecurityErrorResponseWriter.write(response, HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED",
                "Authentication is required and has failed or has not been provided.");
    }
}
