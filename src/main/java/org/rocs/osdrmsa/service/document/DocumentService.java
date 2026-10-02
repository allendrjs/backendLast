package org.rocs.osdrmsa.service.document;

import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.dto.response.DocumentUploadResponse;

public interface DocumentService {
    DocumentUploadResponse processAppealUpload(
            String username, byte[] fileBytes, String filename, String contentType);

    /**
     * Fetches a stored document (file bytes, name, content type) by id, for
     * prefects/staff/admins viewing an attached appeal letter. Throws
     * {@link java.util.NoSuchElementException} if no such document exists.
     */
    Document getById(Long documentId);
}
