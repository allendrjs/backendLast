package org.rocs.osdrmsa.dto.request;

import org.rocs.osdrmsa.domain.department.Department;

/** employeeId required on create, ignored on update (path variable owns it). */
public record EmployeeRequest(String employeeId, Long personId, Department department, String employeeRole) {
}
