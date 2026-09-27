package org.rocs.osdrmsa.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.exception.AccountInactiveException;
import org.rocs.osdrmsa.exception.AccountLockedException;
import org.rocs.osdrmsa.exception.InvalidCredentialsException;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.service.login.LoginService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Login authenticate(String username, String password) {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Username and password are required.");
        }

        Login login = loginRepository.findByUsername(username).orElseThrow(() ->
                new InvalidCredentialsException("Invalid username or password."));

        if (!passwordEncoder.matches(password, login.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        if (login.isLocked()) {
            throw new AccountLockedException("This account is locked. Please contact the OSD office.");
        }

        if (!login.isActive()) {
            throw new AccountInactiveException("Your account has expired. Please contact the OSD office.");
        }

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

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {

        if (username == null || username.isBlank() ||
                currentPassword == null || currentPassword.isBlank() ||
                newPassword == null || newPassword.isBlank()) {

            throw new IllegalArgumentException("All password fields are required.");
        }

        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException("User account not found."));

        if (!passwordEncoder.matches(currentPassword, login.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long."
            );
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter."
            );
        }

        if (!newPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number."
            );
        }

        if (passwordEncoder.matches(newPassword, login.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password."
            );
        }

        login.setPassword(passwordEncoder.encode(newPassword));

        loginRepository.save(login);
    }

}
