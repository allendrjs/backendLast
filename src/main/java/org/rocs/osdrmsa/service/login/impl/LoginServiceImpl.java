package org.rocs.osdrmsa.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.service.login.LoginService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;

    @Override
    public boolean login(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        Optional<Login> login = loginRepository.findByUsername(username);

        if (login.isEmpty()) {
            return false;
        }

        return password.equals(login.get().getPassword());
    }

    @Override
    public Optional<Login> getByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return loginRepository.findByUsername(username);
    }

}
