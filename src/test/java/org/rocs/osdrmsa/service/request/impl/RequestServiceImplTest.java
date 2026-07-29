package org.rocs.osdrmsa.service.request.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;
import org.rocs.osdrmsa.repository.request.RequestRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private Request pendingRequest;

    @BeforeEach
    void setUp() {
        pendingRequest = new Request();
        pendingRequest.setRequestID(1L);
        pendingRequest.setEmployeeID("EMP-001");
        pendingRequest.setType("STUDENT_RECORD");
        pendingRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    void submitRequest_rejectsMissingEmployeeId() {
        Request request = new Request();
        request.setType("STUDENT_RECORD");

        assertThatThrownBy(() -> requestService.submitRequest(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void submitRequest_rejectsMissingType() {
        Request request = new Request();
        request.setEmployeeID("EMP-001");

        assertThatThrownBy(() -> requestService.submitRequest(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void submitRequest_setsStatusToPending() {
        Request request = new Request();
        request.setEmployeeID("EMP-001");
        request.setType("STUDENT_RECORD");

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request saved = requestService.submitRequest(request);

        assertThat(saved.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(saved.getDateProcessed()).isNull();
    }

    @Test
    void processRequest_approvesAndStampsDate() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(requestRepository.save(any(Request.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Request result = requestService.processRequest(1L, RequestStatus.APPROVED, "Approved for review.");

        assertThat(result.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(result.getDateProcessed()).isNotNull();
    }

    @Test
    void processRequest_rejectsDecidingAlreadyProcessedRequest() {
        pendingRequest.setStatus(RequestStatus.APPROVED);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));

        assertThatThrownBy(() -> requestService.processRequest(1L, RequestStatus.DENIED, "too late"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void processRequest_rejectsNonTerminalDecision() {
        // No repository stub here: processRequest validates the decision
        // type before it ever looks the request up, so findById is never
        // called on this path - stubbing it would be an unused stub.
        assertThatThrownBy(() -> requestService.processRequest(1L, RequestStatus.PENDING, "nope"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
