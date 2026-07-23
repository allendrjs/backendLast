package org.rocs.osdrmsa.controller.appeal.dto;

import org.rocs.osdrmsa.domain.appeal.AppealStatus;

public record AppealReviewRequest(AppealStatus status, String remarks) {
}
