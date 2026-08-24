package org.rocs.osdrmsa.dto.summary;

import java.time.LocalDateTime;

public record AiSuggestionSummary(String generatedText, LocalDateTime generatedAt) {
}
