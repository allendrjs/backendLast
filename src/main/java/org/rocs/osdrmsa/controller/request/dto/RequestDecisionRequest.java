package org.rocs.osdrmsa.controller.request.dto;

import org.rocs.osdrmsa.domain.request.RequestStatus;

public record RequestDecisionRequest(RequestStatus decision, String remarks) {
}
