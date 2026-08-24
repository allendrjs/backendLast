package org.rocs.osdrmsa.service.document;

import org.rocs.osdrmsa.dto.response.DocumentUploadResponse;

public interface DocumentService {
    DocumentUploadResponse processAppealUpload(String username, byte[] fileBytes, String filename);
}
