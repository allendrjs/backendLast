package org.rocs.osdrmsa.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.exception.AccountInactiveException;
import org.rocs.osdrmsa.exception.AccountLockedException;
import org.rocs.osdrmsa.exception.InvalidCredentialsException;
import org.rocs.osdrmsa.exception.TooManyAttemptsException;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.service.login.attempt.LoginAttemptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public Login authenticate(String username, String password) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Username and password are required.");
        }

        if (loginAttemptService.hasExceededMaxAttempts(username)) {
            throw new TooManyAttemptsException(
                    "Too many failed login attempts. Please try again in a few minutes.");
        }

        Optional<Login> loginOptional = loginRepository.findByUsername(username);

        if (loginOptional.isEmpty() || !passwordEncoder.matches(password, loginOptional.get().getPassword())) {
            loginAttemptService.addUserToLoginAttemptCache(username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        Login login = loginOptional.get();

        if (login.isLocked()) {
            throw new AccountLockedException("This account is locked. Please contact the OSD office.");
        }

        if (!login.isActive()) {
            throw new AccountInactiveException("This account is inactive. Please contact the OSD office.");
        }

        loginAttemptService.evictUserFromLoginAttemptCache(username);
        login.setLastLoginDate(new Date());
        return loginRepository.save(login);
    }

    @Override
    public Optional<Login> getByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return loginRepository.findByUsername(username);
    }

}
