package org.rocs.osdrmsa.domain.person.student;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.Person;

@Entity
@Data
public class Student {

    @Id
    @Column(name = "STUDENT ID", nullable = false, updatable = false)
    private String studentId;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "STUDENT TYPE", nullable = false)
    private String studentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

}
