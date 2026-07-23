package org.rocs.osdrmsa.repository.disciplinary.action;

import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaryActionRepository extends JpaRepository<DisciplinaryAction, Long> {
}
