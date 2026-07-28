package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.department.Department;

public record EnrollmentResponse(
        Long enrollmentId,
        String studentId,
        String schoolYear,
        String studentLevel,
        String section,
        Department department,
        Long disciplinaryStatusId,
        String disciplinaryStatus
) {
}
