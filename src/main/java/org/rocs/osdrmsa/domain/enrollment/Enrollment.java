package org.rocs.osdrmsa.domain.enrollment;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.department.Department;
import org.rocs.osdrmsa.domain.disciplinary.status.DisciplinaryStatus;
import org.rocs.osdrmsa.domain.person.student.Student;

@Entity
@Data
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollmentId", nullable = false, updatable = false)
    private long enrollmentId;

    @ManyToOne
    @JoinColumn(name = "studentID", nullable = false)
    private Student student;

    @Column(name = "schoolYear")
    private String schoolYear;

    @Column(name = "studentLevel")
    private String studentLevel;

    @Column(name = "section")
    private String section;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "disciplinaryStatusID", nullable = false)
    private DisciplinaryStatus disciplinaryStatus;
}
