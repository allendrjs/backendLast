package org.rocs.osdrmsa.controller.document;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.document.Document;
import org.rocs.osdrmsa.dto.response.DocumentUploadResponse;
import org.rocs.osdrmsa.service.document.DocumentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DocumentUploadResponse> upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        DocumentUploadResponse response = documentService.processAppealUpload(
                authentication.getName(), file.getBytes(), file.getOriginalFilename(), file.getContentType());
        return ResponseEntity.ok(response);
    }

    /**
     * Streams back the raw bytes of a previously uploaded appeal letter, so
     * a prefect/staff/admin client (eventually including the desktop app,
     * via the offline-first plan's Phase 1 API surface) can view it without
     * direct database access.
     */
    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREFECT', 'STAFF')")
    public ResponseEntity<byte[]> download(@PathVariable Long documentId) {
        Document document = documentService.getById(documentId);

        MediaType mediaType;
        try {
            mediaType = document.getContentType() != null
                    ? MediaType.parseMediaType(document.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String filename = document.getFileName() != null ? document.getFileName() : "document";

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(filename).build().toString())
                .body(document.getFileData());
    }
}
