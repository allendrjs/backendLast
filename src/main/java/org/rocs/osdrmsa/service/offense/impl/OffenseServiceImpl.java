package org.rocs.osdrmsa.service.offense.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.repository.offense.OffenseRepository;
import org.rocs.osdrmsa.service.offense.OffenseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OffenseServiceImpl implements OffenseService {

    private final OffenseRepository offenseRepository;

    @Override
    public List<Offense> getAll() {
        return offenseRepository.findAllByOrderByOffenseAsc();
    }

    @Override
    public Optional<Offense> getById(Long id) {
        return offenseRepository.findById(id);
    }

    @Override
    public Optional<Offense> getByName(String offense) {
        return offenseRepository.findByOffense(offense);
    }

    @Override
    public Offense create(Offense offense) {
        offense.setOffenseId(null);
        return offenseRepository.save(offense);
    }

    @Override
    public Offense update(Long id, Offense offense) {
        Offense existing = offenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offense not found: " + id));

        existing.setOffense(offense.getOffense());
        existing.setType(offense.getType());
        existing.setDescription(offense.getDescription());

        return offenseRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        try {
            offenseRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "Cannot delete offense " + id + " because it is referenced by existing records.");
        }
    }
}
