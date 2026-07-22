package org.rocs.osdrmsa.domain.person.guardian;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.person.Person;

@Entity
@Data
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GUARDIAN ID", nullable = false, updatable = false)
    private long guardianID;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Column(name = "CONTACT NUMBER", nullable = false)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship")
    private Relationship relationship;
}
