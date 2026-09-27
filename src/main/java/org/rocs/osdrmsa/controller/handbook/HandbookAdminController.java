package org.rocs.osdrmsa.controller.handbook;

import org.rocs.osdrmsa.service.handbook.HandbookIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/handbook")
public class HandbookAdminController {

    private final HandbookIngestionService handbookIngestionService;

    public HandbookAdminController(HandbookIngestionService handbookIngestionService) {
        this.handbookIngestionService = handbookIngestionService;
    }

    @PostMapping("/ingest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> ingest() {
        String result = handbookIngestionService.ingestAll();
        return ResponseEntity.ok(result);
    }
}

