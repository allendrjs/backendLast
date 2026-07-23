package org.rocs.osdrmsa.service.disciplinary.action;

import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;

import java.util.List;
import java.util.Optional;

public interface DisciplinaryActionService {

    List<DisciplinaryAction> getAll();

    Optional<DisciplinaryAction> getById(Long id);

    DisciplinaryAction create(DisciplinaryAction action);

    DisciplinaryAction update(Long id, DisciplinaryAction action);

    void delete(Long id);
}
