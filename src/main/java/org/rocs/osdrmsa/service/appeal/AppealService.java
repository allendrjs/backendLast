package org.rocs.osdrmsa.service.appeal;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealStatus;

import java.util.List;

public interface AppealService {

    Appeal fileAppeal(Appeal appeal);

    /**
     * Moves an appeal to a new status, validating the transition is legal
     * (FILED -> UNDER_REVIEW -> APPROVED/DENIED only) and stamping
     * dateProcessed when it reaches a terminal state.
     */
    Appeal reviewAppeal(Long appealId, AppealStatus newStatus, String remarks);

    List<Appeal> getByStudentId(String studentId);

    List<Appeal> getByRecordId(Long recordId);
}
