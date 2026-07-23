package org.rocs.osdrmsa.repository.guardian;

import org.rocs.osdrmsa.domain.person.guardian.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
}
