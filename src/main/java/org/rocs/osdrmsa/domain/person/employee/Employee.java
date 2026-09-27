package org.rocs.osdrmsa.domain.person.employee;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.Person;

@Entity
@Data
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @Column(name = "EMPLOYEEID")
    private String employeeId;

    @OneToOne
    @JoinColumn(name = "PERSONID")
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "DEPARTMENT")
    private Department department;

    @Column(name = "EMPLOYEEROLE")
    private String employeeRole;

}