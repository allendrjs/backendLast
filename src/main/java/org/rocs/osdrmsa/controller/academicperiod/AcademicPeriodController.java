package org.rocs.osdrmsa.controller.academicperiod;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.academicperiod.AcademicPeriod;
import org.rocs.osdrmsa.dto.request.AcademicPeriodRequest;
import org.rocs.osdrmsa.service.academicperiod.AcademicPeriodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Lets ROLE_ADMIN configure the appeal-filing window (system plan
 * requirement 9) without a code deploy. Read access is open to ADMIN and
 * PREFECT since prefects benefit from seeing when the window closes;
 * writes are ADMIN-only.
 */
@RestController
@RequestMapping("/api/academic-periods")
@RequiredArgsConstructor
public class AcademicPeriodController {

    private final AcademicPeriodService academicPeriodService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT')")
    public ResponseEntity<List<AcademicPeriod>> getAll() {
        return ResponseEntity.ok(academicPeriodService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> create(@RequestBody AcademicPeriodRequest request) {
        return ResponseEntity.ok(
                academicPeriodService.create(request.label(), request.startDate(), request.endDate(), request.appealDeadline())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> update(@PathVariable Long id, @RequestBody AcademicPeriodRequest request) {
        return ResponseEntity.ok(
                academicPeriodService.update(id, request.label(), request.startDate(), request.endDate(), request.appealDeadline())
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriod> activate(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.activate(id));
    }
}
