package org.rocs.osdrmsa.service.handbook;

import org.rocs.osdrmsa.dto.response.HandbookResponse;

public interface HandbookService {

    /**
     * Returns the full Student Handbook content, merged into readable sections,
     * scoped to the department of the currently logged-in student.
     */
    HandbookResponse getHandbook(String username);
}
