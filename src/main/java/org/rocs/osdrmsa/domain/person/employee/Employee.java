package org.rocs.osdrmsa.domain.person.employee;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.Person;

@Entity
@Data
public class Employee {

    @Id
    @Column(name = "employeeID", nullable = false, updatable = false)
    private String employeeId;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @Column(name = "employeeRole")
    private String employeeRole;

}
