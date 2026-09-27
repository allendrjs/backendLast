package org.rocs.osdrmsa.domain.handbook;

public record HandbookChunk(
        Long chunkId,
        String department,
        String sectionTitle,
        String content
) {
}
