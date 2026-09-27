package org.rocs.osdrmsa.controller.handbook;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.dto.response.HandbookResponse;
import org.rocs.osdrmsa.service.handbook.HandbookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/handbook")
@RequiredArgsConstructor
public class HandbookController {

    private final HandbookService handbookService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<HandbookResponse> getHandbook(Authentication authentication) {
        HandbookResponse response = handbookService.getHandbook(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
