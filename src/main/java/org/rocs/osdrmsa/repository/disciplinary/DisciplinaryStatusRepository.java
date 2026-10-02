package org.rocs.osdrmsa.repository.disciplinary;

import org.rocs.osdrmsa.domain.disciplinary.status.DisciplinaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaryStatusRepository extends JpaRepository<DisciplinaryStatus, Long> {
}
