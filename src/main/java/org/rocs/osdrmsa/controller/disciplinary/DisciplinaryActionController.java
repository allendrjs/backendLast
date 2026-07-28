package org.rocs.osdrmsa.controller.disciplinary;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.dto.request.DisciplinaryActionRequest;
import org.rocs.osdrmsa.dto.response.DisciplinaryActionResponse;
import org.rocs.osdrmsa.service.disciplinary.DisciplinaryActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/** Disciplinary action catalog (sanctions). Same read/write split as Offense. */
@RestController
@RequestMapping("/api/disciplinary-actions")
@RequiredArgsConstructor
public class DisciplinaryActionController {

    private final DisciplinaryActionService disciplinaryActionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<DisciplinaryActionResponse> getAll() {
        return disciplinaryActionService.getAll().stream().map(DisciplinaryActionController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public DisciplinaryActionResponse getById(@PathVariable Long id) {
        return disciplinaryActionService.getById(id).map(DisciplinaryActionController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Disciplinary action not found: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DisciplinaryActionResponse> create(@RequestBody DisciplinaryActionRequest request) {
        DisciplinaryAction entity = new DisciplinaryAction();
        entity.setActionId(request.actionId());
        entity.setAction(request.action());
        entity.setDescription(request.description());
        return ResponseEntity.ok(toResponse(disciplinaryActionService.create(entity)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public DisciplinaryActionResponse update(@PathVariable Long id, @RequestBody DisciplinaryActionRequest request) {
        DisciplinaryAction entity = new DisciplinaryAction();
        entity.setAction(request.action());
        entity.setDescription(request.description());
        return toResponse(disciplinaryActionService.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplinaryActionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static DisciplinaryActionResponse toResponse(DisciplinaryAction action) {
        return new DisciplinaryActionResponse(action.getActionId(), action.getAction(), action.getDescription());
    }
}
