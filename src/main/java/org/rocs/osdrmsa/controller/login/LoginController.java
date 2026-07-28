package org.rocs.osdrmsa.controller.login;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.dto.request.LoginRequest;
import org.rocs.osdrmsa.dto.response.LoginResponse;
import org.rocs.osdrmsa.security.JwtService;
import org.rocs.osdrmsa.service.login.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for Web, Mobile, and Desktop clients to exchange
 * username/password for a JWT. Publicly reachable - see
 * SecurityConfig's PUBLIC_URLS-equivalent matcher for "/login".
 */
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

        Person person = login.getPerson();

        LoginResponse response = new LoginResponse(
                token,
                login.getUsername(),
                login.getRole() != null ? login.getRole().name() : null,
                login.getAuthorities(),
                person != null ? person.getPersonId() : null,
                person != null ? person.getFirstName() : null,
                person != null ? person.getLastName() : null
        );

        return ResponseEntity.ok(response);
    }
}
