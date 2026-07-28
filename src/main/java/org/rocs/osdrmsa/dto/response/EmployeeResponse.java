package org.rocs.osdrmsa.dto.response;

import org.rocs.osdrmsa.domain.department.Department;

public record EmployeeResponse(
        String employeeId,
        Long personId,
        String firstName,
        String lastName,
        Department department,
        String employeeRole
) {
}
