package org.rocs.osdrmsa.domain.appeal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Append-only edit history for an appeal. Per the appeal-lifecycle policy,
 * only ROLE_ADMIN can read this directly -- everyone else (student,
 * prefect, dept head) only sees the boolean {@code edited} flag on
 * {@link Appeal} itself.
 */
@Entity
@Data
@Table(name = "APPEAL_EDIT_HISTORY")
public class AppealEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORYID")
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "APPEALID")
    private Appeal appeal;

    @Lob
    @Column(name = "OLDMESSAGE")
    private String oldMessage;

    @Lob
    @Column(name = "NEWMESSAGE")
    private String newMessage;

    @Column(name = "EDITEDAT")
    private LocalDateTime editedAt;
}
