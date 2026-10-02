package org.rocs.osdrmsa.service.disciplinary.status.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.disciplinary.status.DisciplinaryStatus;
import org.rocs.osdrmsa.repository.disciplinary.DisciplinaryStatusRepository;
import org.rocs.osdrmsa.service.disciplinary.status.DisciplinaryStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisciplinaryStatusServiceImpl implements DisciplinaryStatusService {

    private final DisciplinaryStatusRepository disciplinaryStatusRepository;

    @Override
    public List<DisciplinaryStatus> getAll() {
        return disciplinaryStatusRepository.findAll();
    }

    @Override
    public Optional<DisciplinaryStatus> getById(Long id) {
        return disciplinaryStatusRepository.findById(id);
    }
}
