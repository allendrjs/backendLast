package org.rocs.osdrmsa.service.academicperiod.impl;

import org.rocs.osdrmsa.domain.academicperiod.AcademicPeriod;
import org.rocs.osdrmsa.repository.academicperiod.AcademicPeriodRepository;
import org.rocs.osdrmsa.service.academicperiod.AcademicPeriodService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AcademicPeriodServiceImpl implements AcademicPeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;

    public AcademicPeriodServiceImpl(AcademicPeriodRepository academicPeriodRepository) {
        this.academicPeriodRepository = academicPeriodRepository;
    }

    @Override
    public List<AcademicPeriod> getAll() {
        return academicPeriodRepository.findAllByOrderByStartDateDesc();
    }

    @Override
    public AcademicPeriod create(String label, LocalDate startDate, LocalDate endDate, LocalDate appealDeadline) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("A label is required for the academic period.");
        }

        AcademicPeriod period = new AcademicPeriod();
        period.setLabel(label.trim());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setAppealDeadline(appealDeadline);
        period.setActive(false);

        return academicPeriodRepository.save(period);
    }

    @Override
    public AcademicPeriod update(Long periodId, String label, LocalDate startDate, LocalDate endDate, LocalDate appealDeadline) {
        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new NoSuchElementException("Academic period not found."));

        if (label != null && !label.isBlank()) {
            period.setLabel(label.trim());
        }
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setAppealDeadline(appealDeadline);

        return academicPeriodRepository.save(period);
    }

    @Override
    public AcademicPeriod activate(Long periodId) {
        AcademicPeriod toActivate = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new NoSuchElementException("Academic period not found."));

        academicPeriodRepository.findByActiveTrue().ifPresent(current -> {
            if (!current.getPeriodId().equals(periodId)) {
                current.setActive(false);
                academicPeriodRepository.save(current);
            }
        });

        toActivate.setActive(true);
        return academicPeriodRepository.save(toActivate);
    }
}
