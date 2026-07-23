package org.rocs.osdrmsa.controller.common.dto;

import org.rocs.osdrmsa.domain.record.RecordStatus;

/**
 * A trimmed-down view of a Record for embedding inside other responses
 * (e.g. AppealResponse) without re-nesting the record's full graph.
 */
public record RecordSummary(long recordId, OffenseSummary offense, RecordStatus status) {
}
