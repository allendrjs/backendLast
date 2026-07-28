package org.rocs.osdrmsa.service.offense;

import org.rocs.osdrmsa.domain.offense.Offense;

import java.util.List;
import java.util.Optional;

public interface OffenseService {

    /** All offenses, alphabetical by name - backs dropdown/lookup lists. */
    List<Offense> getAll();

    Optional<Offense> getById(Long id);

    Optional<Offense> getByName(String offense);

    Offense create(Offense offense);

    Offense update(Long id, Offense offense);

    void delete(Long id);
}
