package org.rocs.osdrmsa.service.appeal;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealEditHistory;

import java.util.List;

public interface AppealService {

    List<Appeal> getAppealsByStatus(String status);

    List<Appeal> getAppealsByStudentId(String studentId);

    Appeal submitAppeal(Long recordId, Long enrollmentId, String message, Long documentId);

    Appeal updateAppeal(Long appealId, String newMessage);

    List<AppealEditHistory> getEditHistory(Long appealId);

    void approveAppeal(Long appealId, String remarks);

    void denyAppeal(Long appealId, String remarks);
}
