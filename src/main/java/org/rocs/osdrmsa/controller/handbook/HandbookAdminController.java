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

    /**
     * Re-embeds and reloads the bundled Student Handbook chunks into the database.
     * Run this once after deploying a build that ships a new handbook_chunks.json,
     * or whenever the handbook content changes. Takes a while (one Ollama embedding
     * call per chunk, ~700+ chunks) so expect this request to run for several minutes.
     */
    @PostMapping("/ingest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> ingest() {
        String result = handbookIngestionService.ingestAll();
        return ResponseEntity.ok(result);
    }
}
