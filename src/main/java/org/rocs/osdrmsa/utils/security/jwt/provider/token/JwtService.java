package org.rocs.osdrmsa.utils.security.jwt.provider.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.rocs.osdrmsa.domain.login.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * Issues and validates JWTs for authenticated Login accounts.
 * Token carries the username plus the account's role/authorities as claims
 * so downstream requests can be authorized without a DB round-trip.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_PERSON_ID = "personId";

    /**
     * Must match application.properties' fallback for jwt.secret.
     */
    private static final String INSECURE_DEFAULT_SECRET = "dev-only-insecure-secret-change-me";
    private static final Set<String> SECRET_EXEMPT_PROFILES = Set.of("dev", "local", "test");

    private final Algorithm algorithm;
    private final long expirationMinutes;

    /**
     * Spring-facing constructor. Reads the real active-profiles list off
     * Environment rather than the "spring.profiles.active" property
     */
    @Autowired
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes:60}") long expirationMinutes,
            Environment environment) {
        this(secret, expirationMinutes, String.join(",", environment.getActiveProfiles()));
    }

    /** Testable constructor - unit tests call this directly with a plain profiles string. */
    JwtService(String secret, long expirationMinutes, String activeProfiles) {

        if (INSECURE_DEFAULT_SECRET.equals(secret) && !isExemptProfile(activeProfiles)) {
            throw new IllegalStateException(
                    "Refusing to start: jwt.secret is still the insecure default. "
                            + "Set the JWT_SECRET environment variable, or run under a "
                            + "dev/local/test Spring profile if this is intentional.");
        }

        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMinutes = expirationMinutes;
    }

    private static boolean isExemptProfile(String activeProfiles) {
        if (activeProfiles == null || activeProfiles.isBlank()) {
            return false;
        }
        return Arrays.stream(activeProfiles.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .anyMatch(SECRET_EXEMPT_PROFILES::contains);
    }

    public String generateToken(Login login) {

        Instant now = Instant.now();

        return JWT.create()
                .withSubject(login.getUsername())
                .withClaim(CLAIM_ROLE, login.getRole() != null ? login.getRole().name() : null)
                .withClaim(CLAIM_AUTHORITIES, login.getAuthorities())
                .withClaim(CLAIM_PERSON_ID,
                        login.getPerson() != null ? login.getPerson().getPersonId() : null)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    public Optional<DecodedJWT> verify(String token) {
        try {
            return Optional.of(JWT.require(algorithm).build().verify(token));
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public String extractRole(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim(CLAIM_ROLE).asString();
    }

    /** Raw comma-separated authorities claim, or null if none were set. */
    public String extractAuthorities(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim(CLAIM_AUTHORITIES).asString();
    }
}