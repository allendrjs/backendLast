package org.rocs.osdrmsa.controller.request.mapper;

import org.rocs.osdrmsa.controller.request.dto.RequestResponse;
import org.rocs.osdrmsa.controller.request.dto.RequestSubmitRequest;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.util.DateConversion;

public final class RequestDtoMapper {

    private RequestDtoMapper() {
    }

    public static Request toEntity(RequestSubmitRequest request) {
        Request entity = new Request();
        entity.setEmployeeID(request.employeeId());
        entity.setDetails(request.details());
        entity.setMessage(request.message());
        entity.setType(request.type());
        return entity;
    }

    public static RequestResponse toResponse(Request request) {
        if (request == null) {
            return null;
        }
        return new RequestResponse(
                request.getRequestID(),
                request.getEmployeeID(),
                request.getDetails(),
                request.getMessage(),
                request.getType(),
                request.getStatus(),
                DateConversion.toLocalDate(request.getDateProcessed()),
                request.getRemarks());
    }
}
