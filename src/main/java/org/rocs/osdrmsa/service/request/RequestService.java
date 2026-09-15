package org.rocs.osdrmsa.service.request;

import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.domain.request.RequestStatus;

import java.util.List;

public interface RequestService {

    Request submitRequest(Request request, String username);

    Request processRequest(Long requestId, RequestStatus decision, String remarks);

    List<Request> getByEmployeeId(String employeeId);

    List<Request> getByStatus(RequestStatus status);

    List<Request> getAll();

    List<Request> getMyDepartmentRequests(String username);

    String getMyDepartmentName(String username);
}