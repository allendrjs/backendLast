package org.rocs.osdrmsa.controller.login;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.controller.login.dto.LoginRequest;
import org.rocs.osdrmsa.controller.login.dto.LoginResponse;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.utils.security.jwt.provider.token.JwtService;
import org.rocs.osdrmsa.service.login.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final LoginService loginService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Login login = loginService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(login);

        return ResponseEntity.ok(new LoginResponse(
                token,
                login.getUsername(),
                login.getRole() != null ? login.getRole().name() : null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader("Authorization") String authorizationHeader) {

        String currentToken = stripBearerPrefix(authorizationHeader);
        Optional<DecodedJWT> decoded = jwtService.verify(currentToken);

        if (decoded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(decoded.get());

        return loginService.getByUsername(username)
                .map(login -> ResponseEntity.ok(new LoginResponse(
                        jwtService.generateToken(login),
                        login.getUsername(),
                        login.getRole() != null ? login.getRole().name() : null)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * JWTs issued by this service are stateless and cannot be individually
     * revoked without a server-side blacklist (not implemented here). This
     * endpoint exists for API symmetry; real logout enforcement currently
     * relies on the client discarding the token and the token's short
     * expiration window.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    private String stripBearerPrefix(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return header;
    }
}
