package org.rocs.osdrmsa.dto.request;

/** Payload for resolving a pending record: the sanction imposed plus remarks. */
public record RecordResolveRequest(
        Long actionId,
        String remarks
) {
}
