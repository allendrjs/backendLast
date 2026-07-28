package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.department.Department;

public record StudentResponse(
        String studentId,
        Long personId,
        String firstName,
        String lastName,
        String middleName,
        String address,
        String studentType,
        Department department
) {
}
