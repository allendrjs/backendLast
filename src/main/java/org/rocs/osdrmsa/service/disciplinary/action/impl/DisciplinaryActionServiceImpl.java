package org.rocs.osdrmsa.service.disciplinary.action.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.repository.disciplinary.action.DisciplinaryActionRepository;
import org.rocs.osdrmsa.service.disciplinary.action.DisciplinaryActionService;
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
        return disciplinaryActionRepository.findAll();
    }

    @Override
    public Optional<DisciplinaryAction> getById(Long id) {
        return disciplinaryActionRepository.findById(id);
    }

    @Override
    public DisciplinaryAction create(DisciplinaryAction action) {
        action.setActionId(0);
        return disciplinaryActionRepository.save(action);
    }

    @Override
    public DisciplinaryAction update(Long id, DisciplinaryAction action) {
        DisciplinaryAction existing = disciplinaryActionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Disciplinary action not found: " + id));

        existing.setActionName(action.getActionName());
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
