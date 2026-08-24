package org.rocs.osdrmsa.dto.response;

public record DocumentUploadResponse(
        Long documentId,
        String extractedText,
        String aiSuggestion
) {
}
