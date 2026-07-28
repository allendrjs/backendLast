package org.rocs.osdrmsa.service.disciplinary.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.repository.disciplinary.DisciplinaryActionRepository;
import org.rocs.osdrmsa.service.disciplinary.DisciplinaryActionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisciplinaryActionServiceImpl implements DisciplinaryActionService {

    private final DisciplinaryActionRepository disciplinaryActionRepository;

    @Override
    public List<DisciplinaryAction> getAll() {
        return disciplinaryActionRepository.findAllByOrderByActionAsc();
    }

    @Override
    public Optional<DisciplinaryAction> getById(Long id) {
        return disciplinaryActionRepository.findById(id);
    }

    @Override
    public Optional<DisciplinaryAction> getByName(String action) {
        return disciplinaryActionRepository.findByAction(action);
    }

    @Override
    public DisciplinaryAction create(DisciplinaryAction action) {
        // ACTIONID has no @GeneratedValue in the DB script - it's a manually
        // assigned key, not auto-generated - so the caller must supply a
        // unique actionId. We do NOT zero/null it out here the way the
        // identity-keyed entities do: save() would either collide on a
        // reused id or silently overwrite whatever row already has it.
        if (action.getActionId() == null) {
            throw new IllegalArgumentException("actionId is required and must be unique.");
        }
        if (disciplinaryActionRepository.existsById(action.getActionId())) {
            throw new IllegalArgumentException(
                    "Disciplinary action " + action.getActionId() + " already exists.");
        }
        return disciplinaryActionRepository.save(action);
    }

    @Override
    public DisciplinaryAction update(Long id, DisciplinaryAction action) {
        DisciplinaryAction existing = disciplinaryActionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Disciplinary action not found: " + id));

        existing.setAction(action.getAction());
        existing.setDescription(action.getDescription());

        return disciplinaryActionRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        try {
            disciplinaryActionRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Cannot delete action " + id + " because it is referenced by existing records.");
        }
    }
}
