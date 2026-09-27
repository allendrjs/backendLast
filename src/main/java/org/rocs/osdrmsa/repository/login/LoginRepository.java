package org.rocs.osdrmsa.repository.login;

import org.rocs.osdrmsa.domain.login.Login;
import org.rocs.osdrmsa.domain.login.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByUsername(String username);

    Optional<Login> findByPerson_PersonId(Long personId);

    Optional<Login> findByPerson_PersonIdAndRole(
            Long personId,
            Role role
    );
}