package org.rocs.osdrmsa.controller.appeal.dto;

public record AppealFileRequest(long recordId, long enrollmentId, String message) {
}
