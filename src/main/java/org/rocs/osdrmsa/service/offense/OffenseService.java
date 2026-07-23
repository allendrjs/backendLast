package org.rocs.osdrmsa.service.offense;

import org.rocs.osdrmsa.domain.offense.Offense;

import java.util.List;
import java.util.Optional;

public interface OffenseService {

    List<Offense> getAll();

    List<Offense> getByType(String type);

    Optional<Offense> getById(Long id);

    Offense create(Offense offense);

    Offense update(Long id, Offense offense);

    void delete(Long id);
}
