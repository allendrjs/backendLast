package org.rocs.osdrmsa.controller.appeal.mapper;

import org.rocs.osdrmsa.controller.appeal.dto.AppealFileRequest;
import org.rocs.osdrmsa.controller.appeal.dto.AppealResponse;
import org.rocs.osdrmsa.controller.common.mapper.CommonDtoMapper;
import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.util.DateConversion;

public final class AppealDtoMapper {

    private AppealDtoMapper() {
    }

    public static Appeal toEntity(AppealFileRequest request) {
        Appeal appeal = new Appeal();

        Record record = new Record();
        record.setRecordId(request.recordId());
        appeal.setRecord(record);

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(request.enrollmentId());
        appeal.setEnrollment(enrollment);

        appeal.setMessage(request.message());
        return appeal;
    }

    public static AppealResponse toResponse(Appeal appeal) {
        if (appeal == null) {
            return null;
        }
        return new AppealResponse(
                appeal.getAppealID(),
                CommonDtoMapper.toRecordSummary(appeal.getRecord()),
                CommonDtoMapper.toEnrollmentSummary(appeal.getEnrollment()),
                appeal.getMessage(),
                DateConversion.toLocalDate(appeal.getDateFiled()),
                appeal.getStatus(),
                DateConversion.toLocalDate(appeal.getDateProcessed()),
                appeal.getRemarks());
    }
}
