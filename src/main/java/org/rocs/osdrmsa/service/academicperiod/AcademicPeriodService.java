package org.rocs.osdrmsa.service.academicperiod;

import org.rocs.osdrmsa.domain.academicperiod.AcademicPeriod;

import java.time.LocalDate;
import java.util.List;

public interface AcademicPeriodService {

    List<AcademicPeriod> getAll();

    AcademicPeriod create(String label, LocalDate startDate, LocalDate endDate, LocalDate appealDeadline);

    AcademicPeriod update(Long periodId, String label, LocalDate startDate, LocalDate endDate, LocalDate appealDeadline);

    AcademicPeriod activate(Long periodId);
}
