package org.rocs.osdrmsa.controller.disciplinary.status;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.status.DisciplinaryStatus;
import org.rocs.osdrmsa.service.disciplinary.status.DisciplinaryStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinary-statuses")
@RequiredArgsConstructor
public class DisciplinaryStatusController {

    private final DisciplinaryStatusService disciplinaryStatusService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DisciplinaryStatus>> getAll() {
        return ResponseEntity.ok(disciplinaryStatusService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DisciplinaryStatus> getById(@PathVariable Long id) {
        return disciplinaryStatusService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
