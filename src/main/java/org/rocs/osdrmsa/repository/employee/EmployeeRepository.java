package org.rocs.osdrmsa.repository.employee;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByDepartment(Department department);
}
