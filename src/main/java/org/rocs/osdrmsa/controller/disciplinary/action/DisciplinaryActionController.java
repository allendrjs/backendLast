package org.rocs.osdrmsa.controller.disciplinary.action;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.service.disciplinary.action.DisciplinaryActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinary-actions")
@RequiredArgsConstructor
public class DisciplinaryActionController {

    private final DisciplinaryActionService disciplinaryActionService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DisciplinaryAction>> getAll() {
        return ResponseEntity.ok(disciplinaryActionService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DisciplinaryAction> getById(@PathVariable Long id) {
        return disciplinaryActionService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisciplinaryAction> create(@RequestBody DisciplinaryAction action) {
        return ResponseEntity.ok(disciplinaryActionService.create(action));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisciplinaryAction> update(
            @PathVariable Long id, @RequestBody DisciplinaryAction action) {
        return ResponseEntity.ok(disciplinaryActionService.update(id, action));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplinaryActionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
