package org.rocs.osdrmsa.domain.person.student;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.person.Person;

@Entity
@Data
public class Student {

    @Id
    @Column(name = "studentID", nullable = false, updatable = false)
    private String studentId;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Column(name = "address")
    private String address;

    @Column(name = "studentType")
    private String studentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @ManyToMany
    @JoinTable(
            name = "studentGuardian",
            joinColumns = @JoinColumn(name = "studentID"),
            inverseJoinColumns = @JoinColumn(name = "guardianID"))
    private java.util.List<org.rocs.osdrmsa.domain.person.guardian.Guardian> guardians = new java.util.ArrayList<>();

}
