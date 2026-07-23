package org.rocs.osdrmsa.repository.offense;

import org.rocs.osdrmsa.domain.offense.Offense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffenseRepository extends JpaRepository<Offense, Long> {

    List<Offense> findByType(String type);
}
