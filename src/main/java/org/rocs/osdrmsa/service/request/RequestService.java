package org.rocs.osdrmsa.service.request;

import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.util.List;

public interface RequestService {

    Request submitRequest(Request request);

    /**
     * Prefect/Admin decision on a pending request. Only PENDING requests
     * can be processed; APPROVED/DENIED are terminal.
     */
    Request processRequest(Long requestId, RequestStatus decision, String remarks);

    List<Request> getByEmployeeId(String employeeId);

    List<Request> getByStatus(RequestStatus status);

    List<Request> getAll();
}
