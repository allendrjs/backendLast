package org.rocs.osdrmsa.service.employee;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    List<Employee> getAll();

    List<Employee> getByDepartment(Department department);

    Optional<Employee> getById(String employeeId);

    Employee create(Employee employee);

    Employee update(String employeeId, Employee employee);

    void delete(String employeeId);
}
