package org.rocs.osdrmsa.controller.login;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.dto.request.ChangePasswordRequest;
import org.rocs.osdrmsa.dto.request.LoginRequest;
import org.rocs.osdrmsa.dto.response.LoginResponse;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.utils.security.constant.SecurityConstant;
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

    private final LoginService loginService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Login login = loginService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(login);

        return ResponseEntity.ok(new LoginResponse(token, login.getUsername(), login.getRole() != null ? login.getRole().name() : null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader(SecurityConstant.AUTH_HEADER) String authorizationHeader) {

        String currentToken = stripBearerPrefix(authorizationHeader);

        Optional<DecodedJWT> decoded = jwtService.verify(currentToken);

        if (decoded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(decoded.get());

        Optional<Login> loginOptional = loginService.getByUsername(username);

        if (loginOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Login login = loginOptional.get();

        if (login.isLocked() || !login.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(login);

        return ResponseEntity.ok(
                new LoginResponse(token, login.getUsername(), login.getRole() != null ? login.getRole().name() : null)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    private String stripBearerPrefix(String header) {
        if (header != null && header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            return header.substring(SecurityConstant.TOKEN_PREFIX.length());
        }
        return header;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestHeader(SecurityConstant.AUTH_HEADER) String authorizationHeader,
                                               @RequestBody ChangePasswordRequest request) {

        String currentToken = stripBearerPrefix(authorizationHeader);

        Optional<DecodedJWT> decoded = jwtService.verify(currentToken);

        if (decoded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(decoded.get());

        loginService.changePassword(username, request.currentPassword(), request.newPassword());

        return ResponseEntity.ok().build();
    }
}
