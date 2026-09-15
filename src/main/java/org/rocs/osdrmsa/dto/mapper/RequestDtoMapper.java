package org.rocs.osdrmsa.dto.mapper;

import org.rocs.osdrmsa.dto.response.RequestResponse;
import org.rocs.osdrmsa.dto.request.RequestSubmitRequest;
import org.rocs.osdrmsa.domain.request.Request;
import org.rocs.osdrmsa.utils.converter.DateConversion;

public final class RequestDtoMapper {

    private RequestDtoMapper() {}

    public static Request toEntity(RequestSubmitRequest request) {
        Request entity = new Request();

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
                request.getDateFiled(),
                DateConversion.toLocalDate(request.getDateProcessed()),
                request.getRemarks(),
                request.getAiResponse()
        );
    }
}