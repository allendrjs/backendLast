package org.rocs.osdrmsa.repository.academicperiod;

import org.rocs.osdrmsa.domain.academicperiod.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    Optional<AcademicPeriod> findByActiveTrue();

    List<AcademicPeriod> findAllByOrderByStartDateDesc();
}
