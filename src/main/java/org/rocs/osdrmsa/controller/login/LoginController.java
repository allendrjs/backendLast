package org.rocs.osdrmsa.controller.login;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.service.login.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Login login) {

        boolean authenticated = loginService.login(
                login.getUsername(),
                login.getPassword());

        if (!authenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }

}
