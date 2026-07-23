package org.rocs.osdrmsa.controller.common.dto;

import org.rocs.osdrmsa.domain.department.Department;

public record EnrollmentSummary(
        long enrollmentId,
        StudentSummary student,
        String schoolYear,
        String studentLevel,
        String section,
        Department department) {
}
