package org.rocs.osdrmsa.repository.employee;

import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByDepartment(Department department);

    /**
     * Resolves an Employee by the Person it's linked to (Employee.person is
     * a OneToOne on PERSONID). Used to look up the logged-in Prefect/Admin's
     * own employeeId from the JWT's personId claim - same pattern as
     * StudentRepository.findByPerson_PersonId.
     */
    Optional<Employee> findByPerson_PersonId(Long personId);
}
