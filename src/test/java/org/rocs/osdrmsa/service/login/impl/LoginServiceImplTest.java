package org.rocs.osdrmsa.service.login.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.login.Role;
import org.rocs.osdrmsa.exception.AccountInactiveException;
import org.rocs.osdrmsa.exception.AccountLockedException;
import org.rocs.osdrmsa.exception.InvalidCredentialsException;
import org.rocs.osdrmsa.repository.login.LoginRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginServiceImpl loginService;

    private Login activeLogin;

    @BeforeEach
    void setUp() {
        activeLogin = new Login();
        activeLogin.setUsername("jdoe");
        activeLogin.setPassword("hashed-password");
        activeLogin.setRole(Role.ROLE_USER);
        activeLogin.setActive(true);
        activeLogin.setLocked(false);
    }

    @Test
    void authenticate_rejectsBlankCredentials() {
        assertThatThrownBy(() -> loginService.authenticate("", "pw"))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThatThrownBy(() -> loginService.authenticate("jdoe", ""))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_rejectsUnknownUsername() {
        when(loginRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.authenticate("ghost", "pw"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_rejectsWrongPassword() {
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> loginService.authenticate("jdoe", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_rejectsLockedAccount() {
        activeLogin.setLocked(true);
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("correct", "hashed-password")).thenReturn(true);

        assertThatThrownBy(() -> loginService.authenticate("jdoe", "correct"))
                .isInstanceOf(AccountLockedException.class);
    }

    @Test
    void authenticate_rejectsInactiveAccount() {
        activeLogin.setActive(false);
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("correct", "hashed-password")).thenReturn(true);

        assertThatThrownBy(() -> loginService.authenticate("jdoe", "correct"))
                .isInstanceOf(AccountInactiveException.class);
    }

    @Test
    void authenticate_succeedsAndStampsLastLoginDate() {
        when(loginRepository.findByUsername("jdoe")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("correct", "hashed-password")).thenReturn(true);
        when(loginRepository.save(any(Login.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Login result = loginService.authenticate("jdoe", "correct");

        assertThat(result.getUsername()).isEqualTo("jdoe");
        assertThat(result.getLastLoginDate()).isNotNull();
    }
}
