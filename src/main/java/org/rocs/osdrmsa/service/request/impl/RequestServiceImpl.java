package org.rocs.osdrmsa.service.request.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.repository.request.RequestRepository;
import org.rocs.osdrmsa.service.request.RequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Override
    public List<Request> getByEmployeeId(String employeeId) {
        return requestRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public List<Request> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    public Optional<Request> getById(Long requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public Request create(Request request) {
        request.setRequestId(null);
        request.setStatus(RequestStatus.PENDING);
        request.setDateProcessed(null);
        request.setRemarks(null);
        return requestRepository.save(request);
    }

    @Override
    public Request approve(Long requestId, String remarks) {
        Request existing = findOrThrow(requestId);
        existing.setStatus(RequestStatus.RESOLVED);
        existing.setRemarks(remarks != null && !remarks.isBlank() ? remarks : "Approved.");
        existing.setDateProcessed(LocalDate.now());
        return requestRepository.save(existing);
    }

    @Override
    public Request deny(Long requestId, String remarks) {
        if (remarks == null || remarks.isBlank()) {
            throw new IllegalArgumentException("Denial remarks are required.");
        }
        Request existing = findOrThrow(requestId);
        existing.setStatus(RequestStatus.RESOLVED);
        existing.setRemarks("Denied: " + remarks);
        existing.setDateProcessed(LocalDate.now());
        return requestRepository.save(existing);
    }

    private Request findOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found: " + requestId));
    }
}
