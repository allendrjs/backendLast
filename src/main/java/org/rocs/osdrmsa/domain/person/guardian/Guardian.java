package org.rocs.osdrmsa.domain.person.guardian;

import jakarta.persistence.*;
import lombok.Data;
import org.rocs.osdrmsa.domain.person.Person;

/**
 * relationship is a plain String (not a Java enum) because the DB script's
 * CHECK constraint uses mixed-case values ('Father', 'Mother', 'Guardian')
 * rather than the all-caps convention @Enumerated(STRING) would expect.
 */
@Entity
@Data
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guardianID", nullable = false, updatable = false)
    private long guardianID;

    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "relationship")
    private String relationship;
}
