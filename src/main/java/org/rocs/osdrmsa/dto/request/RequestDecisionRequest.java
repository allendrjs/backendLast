package org.rocs.osdrmsa.dto.request;

/** Payload for a Prefect approving or denying a record request. */
public record RequestDecisionRequest(
        String remarks
) {
}
