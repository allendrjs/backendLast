package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.service.record.OffenseFrequency;

import java.util.List;

public record RecordStatsResponse(
        String schoolYear,
        long totalForSchoolYear,
        long filedToday,
        List<OffenseFrequency> offenseFrequency
) {
}
