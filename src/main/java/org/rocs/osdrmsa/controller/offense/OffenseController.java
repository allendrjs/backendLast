package org.rocs.osdrmsa.controller.offense;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.dto.request.OffenseRequest;
import org.rocs.osdrmsa.dto.response.OffenseResponse;
import org.rocs.osdrmsa.service.offense.OffenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Offense catalog (reference data). Everyone who needs to file or review a
 * violation needs to read this list, so GETs are open to all staff roles;
 * only Admin maintains the catalog itself.
 */
@RestController
@RequestMapping("/api/offenses")
@RequiredArgsConstructor
public class OffenseController {

    private final OffenseService offenseService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public List<OffenseResponse> getAll() {
        return offenseService.getAll().stream().map(OffenseController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_PREFECT','ROLE_STAFF')")
    public OffenseResponse getById(@PathVariable Long id) {
        return offenseService.getById(id).map(OffenseController::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Offense not found: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OffenseResponse> create(@RequestBody OffenseRequest request) {
        Offense saved = offenseService.create(toEntity(null, request));
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OffenseResponse update(@PathVariable Long id, @RequestBody OffenseRequest request) {
        return toResponse(offenseService.update(id, toEntity(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static Offense toEntity(Long id, OffenseRequest request) {
        Offense offense = new Offense();
        offense.setOffenseId(id);
        offense.setOffense(request.offense());
        offense.setType(request.type());
        offense.setDescription(request.description());
        return offense;
    }

    private static OffenseResponse toResponse(Offense offense) {
        return new OffenseResponse(offense.getOffenseId(), offense.getOffense(), offense.getType(), offense.getDescription());
    }
}
