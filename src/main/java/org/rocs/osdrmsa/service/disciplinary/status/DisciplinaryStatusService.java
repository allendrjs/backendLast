package org.rocs.osdrmsa.service.disciplinary.status;

import org.rocs.osdrmsa.domain.disciplinary.status.DisciplinaryStatus;

import java.util.List;
import java.util.Optional;

public interface DisciplinaryStatusService {

    List<DisciplinaryStatus> getAll();

    Optional<DisciplinaryStatus> getById(Long id);
}
