package org.rocs.osdrmsa.domain.person;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "PERSON")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSONID")
    private Long personId;

    @Column(name = "LASTNAME", nullable = false, length = 50)
    private String lastName;

    @Column(name = "FIRSTNAME", nullable = false, length = 100)
    private String firstName;

    @Column(name = "MIDDLENAME", length = 30)
    private String middleName;

    @Column(name = "DATEOFBIRTH")
    private LocalDate dateOfBirth;
}
