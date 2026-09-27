package org.rocs.osdrmsa.dto.request;

public record AppealFileRequest(Long recordId, Long enrollmentId, String message, Long documentId) {
}
