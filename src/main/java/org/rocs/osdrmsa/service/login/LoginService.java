package org.rocs.osdrmsa.service.login;

import org.rocs.osdrmsa.domain.login.Login;

import java.util.Optional;

public interface LoginService {

    boolean login(String username, String password);

    Optional<Login> getByUsername(String username);
}
