package org.rocs.osdrmsa.domain.person;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERSON ID", nullable = false, updatable = false)
    private long personID;
    @Column(name = "LASTNAME", nullable = false)
    private String lastName;
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "MIDDLENAME", nullable = false)
    private String middleName;
}
