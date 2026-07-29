package org.rocs.osdrmsa.utils.security.jwt.filter.authentication.access.denied;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rocs.osdrmsa.utils.security.jwt.filter.authentication.SecurityErrorResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles authenticated-but-insufficient-role denials at the filter-chain
 * level (as opposed to @PreAuthorize denials inside a controller method,
 * which GlobalExceptionHandler already handles via its own
 * AccessDeniedException mapping). Kept in sync with that handler's JSON
 * shape and status code (403) - unlike the reference implementation on the
 * SECURITY-Security-Constants branch, which has this and the 401 case
 * swapped.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        SecurityErrorResponseWriter.write(response, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You do not have permission to perform this action.");
    }
}
