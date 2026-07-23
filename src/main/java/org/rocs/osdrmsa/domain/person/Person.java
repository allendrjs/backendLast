package org.rocs.osdrmsa.domain.person;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personID", nullable = false, updatable = false)
    private long personID;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "middleName")
    private String middleName;
}
