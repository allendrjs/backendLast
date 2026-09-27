package org.rocs.osdrmsa.dto.response;

import java.util.List;

public record HandbookResponse(
        String department,
        List<HandbookSectionResponse> sections
) {
}
