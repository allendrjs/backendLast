package org.rocs.osdrmsa.service.login;

import org.rocs.osdrmsa.domain.login.Login;

import java.util.Optional;

public interface LoginService {

    /**
     * Validates credentials and enforces active/locked status.
     * Throws InvalidCredentialsException, AccountLockedException, or
     * AccountInactiveException on failure so callers can surface a
     * distinct message for each case.
     */
    Login authenticate(String username, String password);

    Optional<Login> getByUsername(String username);
}
