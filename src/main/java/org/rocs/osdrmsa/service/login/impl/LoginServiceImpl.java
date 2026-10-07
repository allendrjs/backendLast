package org.rocs.osdrmsa.service.login.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.login.Role;
import org.rocs.osdrmsa.dto.response.LockedAccountResponse;
import org.rocs.osdrmsa.exception.AccountInactiveException;
import org.rocs.osdrmsa.exception.AccountLockedException;
import org.rocs.osdrmsa.exception.InvalidCredentialsException;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.rocs.osdrmsa.repository.student.StudentRepository;
import org.rocs.osdrmsa.service.login.LoginService;
import org.rocs.osdrmsa.utils.security.DefaultCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final String REACTIVATION_MESSAGE =
            "Proceed to prefect office to request for account reactivation";

    private static final int MAX_ATTEMPTS = 5;
    private static final long ADMIN_LOCK_MILLIS = 15 * 60 * 1000L;

    private final Map<String, Integer> adminFailedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> adminLockedUntil = new ConcurrentHashMap<>();

    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Login authenticate(String username, String password) {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {

            throw new InvalidCredentialsException("Username and password are required.");
        }

        Login login = loginRepository.findByUsername(username).orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));

        boolean admin = login.getRole() == Role.ROLE_ADMIN;

        if (admin) {
            return authenticateAdmin(login, password);
        }

        if (login.isLocked()) {
            throw new AccountLockedException(REACTIVATION_MESSAGE);
        }

        if (!login.isActive()) {
            throw new AccountInactiveException(REACTIVATION_MESSAGE);
        }

        if (!passwordEncoder.matches(password, login.getPassword())) {

            int failedAttempts = login.getFailedLoginAttempts() + 1;

            login.setFailedLoginAttempts(failedAttempts);

            if (failedAttempts >= MAX_ATTEMPTS) {
                login.setLocked(true);
                login.setActive(false);

                loginRepository.save(login);
                syncStudentActive(login, false);

                throw new AccountLockedException(REACTIVATION_MESSAGE);
            }

            loginRepository.save(login);

            throw new InvalidCredentialsException(
                    "Invalid username or password. " +
                            "Attempt " + failedAttempts + " of " + MAX_ATTEMPTS + "."
            );
        }

        login.setFailedLoginAttempts(0);
        login.setLastLoginDate(new Date());

        return loginRepository.save(login);
    }

    private Login authenticateAdmin(Login login, String password) {
        String key = login.getUsername().toLowerCase();
        long now = System.currentTimeMillis();

        Long lockedUntil = adminLockedUntil.get(key);
        if (lockedUntil != null) {
            if (now < lockedUntil) {
                long minutes = Math.max(1, (lockedUntil - now + 59_999) / 60_000);
                throw new AccountLockedException(
                        "Too many failed attempts. Try again in " + minutes + " minute(s).");
            }
            adminLockedUntil.remove(key);
            adminFailedAttempts.remove(key);
        }

        if (!passwordEncoder.matches(password, login.getPassword())) {
            int failed = adminFailedAttempts.merge(key, 1, Integer::sum);

            if (failed >= MAX_ATTEMPTS) {
                adminLockedUntil.put(key, now + ADMIN_LOCK_MILLIS);
                adminFailedAttempts.remove(key);
                throw new AccountLockedException(
                        "Too many failed attempts. Try again in 15 minute(s).");
            }

            throw new InvalidCredentialsException(
                    "Invalid username or password. Attempt " + failed + " of " + MAX_ATTEMPTS + ".");
        }

        adminFailedAttempts.remove(key);

        login.setLocked(false);
        login.setActive(true);
        login.setFailedLoginAttempts(0);
        login.setLastLoginDate(new Date());

        return loginRepository.save(login);
    }

    private void syncStudentActive(Login login, boolean active) {
        if (login.getRole() != Role.ROLE_USER || login.getPerson() == null
                || login.getPerson().getPersonId() == null) {
            return;
        }

        studentRepository.findByPerson_PersonId(login.getPerson().getPersonId())
                .ifPresent(student -> {
                    student.setActive(active);
                    studentRepository.save(student);
                });
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
        syncStudentActive(login, true);
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

