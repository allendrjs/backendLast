package org.rocs.osdrmsa.dto.request;

import org.rocs.osdrmsa.domain.department.Department;

/**
 * studentId is required on create (it's the entity's natural key, not
 * auto-generated) and ignored on update, since the path variable owns it.
 */
public record StudentRequest(
        String studentId,
        Long personId,
        String address,
        String studentType,
        Department department
) {
}
