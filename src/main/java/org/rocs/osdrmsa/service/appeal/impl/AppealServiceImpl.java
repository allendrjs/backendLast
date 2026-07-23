package org.rocs.osdrmsa.service.appeal.impl;

import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.appeal.AppealStatus;
import org.rocs.osdrmsa.repository.appeal.AppealRepository;
import org.rocs.osdrmsa.service.appeal.AppealService;
import org.rocs.osdrmsa.service.audit.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {

    /**
     * Legal forward transitions. An appeal starts at FILED and can only
     * move to UNDER_REVIEW, then to a terminal APPROVED/DENIED state.
     * There is no path back to an earlier state.
     */
    private static final Map<AppealStatus, Set<AppealStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(AppealStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(AppealStatus.FILED, EnumSet.of(AppealStatus.UNDER_REVIEW));
        ALLOWED_TRANSITIONS.put(AppealStatus.UNDER_REVIEW,
                EnumSet.of(AppealStatus.APPROVED, AppealStatus.DENIED));
        ALLOWED_TRANSITIONS.put(AppealStatus.APPROVED, EnumSet.noneOf(AppealStatus.class));
        ALLOWED_TRANSITIONS.put(AppealStatus.DENIED, EnumSet.noneOf(AppealStatus.class));
    }

    private static final String ENTITY_TYPE = "Appeal";

    private final AppealRepository appealRepository;
    private final AuditLogService auditLogService;

    @Override
    public Appeal fileAppeal(Appeal appeal) {

        if (appeal.getRecord() == null || appeal.getEnrollment() == null) {
            throw new IllegalArgumentException("An appeal must reference a record and enrollment.");
        }
        if (appeal.getMessage() == null || appeal.getMessage().isBlank()) {
            throw new IllegalArgumentException("Appeal message is required.");
        }

        appeal.setAppealID(0);
        appeal.setStatus(AppealStatus.FILED);
        appeal.setDateFiled(new Date());
        appeal.setDateProcessed(null);
        appeal.setRemarks(null);

        Appeal saved = appealRepository.save(appeal);
        auditLogService.log("APPEAL_FILED", ENTITY_TYPE, String.valueOf(saved.getAppealID()), null);
        return saved;
    }

    @Override
    public Appeal reviewAppeal(Long appealId, AppealStatus newStatus, String remarks) {

        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new NoSuchElementException("Appeal not found: " + appealId));

        Set<AppealStatus> allowedNext = ALLOWED_TRANSITIONS.getOrDefault(
                appeal.getStatus(), EnumSet.noneOf(AppealStatus.class));

        if (!allowedNext.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Cannot move appeal " + appealId + " from " + appeal.getStatus()
                            + " to " + newStatus + ".");
        }

        appeal.setStatus(newStatus);
        appeal.setRemarks(remarks);

        if (newStatus == AppealStatus.APPROVED || newStatus == AppealStatus.DENIED) {
            appeal.setDateProcessed(new Date());
        }

        Appeal saved = appealRepository.save(appeal);
        auditLogService.log("APPEAL_" + newStatus, ENTITY_TYPE, String.valueOf(saved.getAppealID()), remarks);
        return saved;
    }

    @Override
    public List<Appeal> getByStudentId(String studentId) {
        return appealRepository.findByEnrollmentStudentStudentId(studentId);
    }

    @Override
    public List<Appeal> getByRecordId(Long recordId) {
        return appealRepository.findByRecordRecordId(recordId);
    }
}
