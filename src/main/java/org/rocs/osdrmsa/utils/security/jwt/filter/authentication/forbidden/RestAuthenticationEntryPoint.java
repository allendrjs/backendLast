package org.rocs.osdrmsa.utils.security.jwt.filter.authentication.forbidden;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rocs.osdrmsa.utils.security.SecurityErrorResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles requests that reach the filter chain with no, invalid, or expired
 * JWT
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