package org.rocs.osdrmsa.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.login.Role;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("unit-test-secret", 60, "test");

    private Login sampleLogin() {
        Login login = new Login();
        login.setUsername("jdoe");
        login.setRole(Role.ROLE_PREFECT);
        return login;
    }

    @Test
    void generateThenVerify_roundTripsClaims() {
        String token = jwtService.generateToken(sampleLogin());

        Optional<DecodedJWT> decoded = jwtService.verify(token);

        assertThat(decoded).isPresent();
        assertThat(jwtService.extractUsername(decoded.get())).isEqualTo("jdoe");
        assertThat(jwtService.extractRole(decoded.get())).isEqualTo("ROLE_PREFECT");
    }

    @Test
    void verify_rejectsGarbageToken() {
        assertThat(jwtService.verify("not-a-real-token")).isEmpty();
    }

    @Test
    void verify_rejectsTokenSignedWithDifferentSecret() {
        JwtService otherService = new JwtService("a-completely-different-secret", 60, "test");
        String token = otherService.generateToken(sampleLogin());

        assertThat(jwtService.verify(token)).isEmpty();
    }

    @Test
    void verify_rejectsExpiredToken() {
        // Negative expiration guarantees the token is already expired by the
        // time verify() runs, avoiding millisecond-timing flakiness.
        JwtService alreadyExpiredService = new JwtService("unit-test-secret", -1, "test");
        String token = alreadyExpiredService.generateToken(sampleLogin());

        assertThat(alreadyExpiredService.verify(token)).isEmpty();
    }

    @Test
    void constructor_rejectsInsecureDefaultSecretOutsideExemptProfiles() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> new JwtService("dev-only-insecure-secret-change-me", 60, "prod"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> new JwtService("dev-only-insecure-secret-change-me", 60, ""));
    }

    @Test
    void constructor_allowsInsecureDefaultSecretUnderDevProfile() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> new JwtService("dev-only-insecure-secret-change-me", 60, "dev"));
    }
}
