package org.rocs.osdrmsa.controller.offense;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.service.offense.OffenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offenses")
@RequiredArgsConstructor
public class OffenseController {

    private final OffenseService offenseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Offense>> getAll(
            @RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(offenseService.getByType(type));
        }
        return ResponseEntity.ok(offenseService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Offense> getById(@PathVariable Long id) {
        return offenseService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Offense> create(@RequestBody Offense offense) {
        return ResponseEntity.ok(offenseService.create(offense));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Offense> update(@PathVariable Long id, @RequestBody Offense offense) {
        return ResponseEntity.ok(offenseService.update(id, offense));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
