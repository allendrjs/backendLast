package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.person.guardian.Relationship;

public record GuardianResponse(
        Long guardianId,
        Long personId,
        String firstName,
        String lastName,
        String contactNumber,
        Relationship relationship
) {
}
