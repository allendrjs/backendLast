package org.rocs.osdrmsa.utils.security.constant;

/**
 * Central repository of security-related constants used throughout the
 * application.
 */
public final class SecurityConstant {

    private SecurityConstant() {
    }

    /** The HTTP header carrying the bearer token. */
    public static final String AUTH_HEADER = "Authorization";

    /** The prefix expected before the raw JWT in the Authorization header. */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** URL patterns reachable without a valid JWT. */
    public static final String[] PUBLIC_URLS = {
            "/login", "/login/**", "/api/health",
            "/swagger-ui.html", "/swagger-ui/**",
            "/v3/api-docs", "/v3/api-docs/**"
    };
}
