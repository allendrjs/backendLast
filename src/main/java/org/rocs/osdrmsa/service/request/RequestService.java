package org.rocs.osdrmsa.service.request;

import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    List<Request> getByEmployeeId(String employeeId);

    List<Request> getByStatus(RequestStatus status);

    Optional<Request> getById(Long requestId);

    /** Creates a new record request, status defaults to PENDING. */
    Request create(Request request);

    /** Marks the request RESOLVED with an approval outcome recorded in remarks. */
    Request approve(Long requestId, String remarks);

    /** Marks the request RESOLVED with a denial outcome; remarks are required. */
    Request deny(Long requestId, String remarks);
}
