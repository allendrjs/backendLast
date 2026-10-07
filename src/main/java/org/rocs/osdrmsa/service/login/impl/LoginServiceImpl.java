package org.rocs.osdrmsa.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.dto.response.LockedAccountResponse;
import org.rocs.osdrmsa.exception.AccountInactiveException;
import org.rocs.osdrmsa.exception.AccountLockedException;
import org.rocs.osdrmsa.exception.InvalidCredentialsException;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.utils.security.DefaultCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final String REACTIVATION_MESSAGE =
            "Proceed to prefect office to request for account reactivation";

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Login authenticate(String username, String password) {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {

            throw new InvalidCredentialsException("Username and password are required.");
        }

        Login login = loginRepository.findByUsername(username).orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));

        if (login.isLocked()) {
            throw new AccountLockedException(REACTIVATION_MESSAGE);
        }

        if (!passwordEncoder.matches(password, login.getPassword())) {

            int failedAttempts = login.getFailedLoginAttempts() + 1;

            login.setFailedLoginAttempts(failedAttempts);

            if (failedAttempts >= 5) {
                login.setLocked(true);
                login.setActive(false);

                loginRepository.save(login);

                throw new AccountLockedException(REACTIVATION_MESSAGE);
            }

            loginRepository.save(login);

            throw new InvalidCredentialsException(
                    "Invalid username or password. " +
                            "Attempt " + failedAttempts + " of 5."
            );
        }

        if (!login.isActive()) {
            throw new AccountInactiveException(REACTIVATION_MESSAGE);
        }

        login.setFailedLoginAttempts(0);
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
    public List<LockedAccountResponse> getLockedAccounts() {

        return loginRepository.findByLockedTrueOrActiveFalse()
                .stream()
                .map(login -> new LockedAccountResponse(
                        login.getUsername(),
                        login.getRole() != null ? login.getRole().name() : null,
                        login.getFailedLoginAttempts(),
                        login.isLocked(),
                        login.isActive()
                ))
                .toList();
    }

    @Override
    public void unlockAccount(String username) {
        Login login = loginRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User account not found."));

        if (login.getPerson() == null) {
            throw new IllegalArgumentException("No person record found for this account.");
        }

        String lastName = DefaultCredentials.passwordFor(login.getPerson().getLastName());

        login.setPassword(passwordEncoder.encode(lastName));
        login.setLocked(false);
        login.setActive(true);
        login.setFailedLoginAttempts(0);

        loginRepository.save(login);
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {

        if (username == null || username.isBlank() ||
                currentPassword == null || currentPassword.isBlank() ||
                newPassword == null || newPassword.isBlank()) {

            throw new IllegalArgumentException("All password fields are required.");
        }

        Login login = loginRepository.findByUsername(username).orElseThrow(() -> new InvalidCredentialsException("User account not found."));

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

