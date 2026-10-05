package org.rocs.osdrmsa.service.login;

import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.dto.response.LockedAccountResponse;

import java.util.List;
import java.util.Optional;

public interface LoginService {

    Login authenticate(String username, String password);

    Optional<Login> getByUsername(String username);

    void changePassword(String username, String currentPassword, String newPassword);

    List<LockedAccountResponse> getLockedAccounts();

    void unlockAccount(String username);
}

