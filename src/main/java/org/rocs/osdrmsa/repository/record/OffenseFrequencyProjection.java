package org.rocs.osdrmsa.repository.record;

/**
 * Projection for {@link RecordRepository#findOffenseFrequencyBySchoolYear},
 * one row per distinct offense with how many records reference it in a
 * given school year, ordered by count descending.
 */
public interface OffenseFrequencyProjection {

    String getOffense();

    long getTotal();
}
