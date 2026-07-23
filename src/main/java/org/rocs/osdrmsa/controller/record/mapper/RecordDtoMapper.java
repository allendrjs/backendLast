package org.rocs.osdrmsa.controller.record.mapper;

import org.rocs.osdrmsa.controller.common.mapper.CommonDtoMapper;
import org.rocs.osdrmsa.controller.record.dto.RecordCreateRequest;
import org.rocs.osdrmsa.controller.record.dto.RecordResponse;
import org.rocs.osdrmsa.controller.record.dto.RecordUpdateRequest;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.util.DateConversion;

import java.time.LocalDate;

/**
 * Converts between the wire-facing Record DTOs and the JPA entity. Requests
 * build "reference" entities carrying only the FK id for each association -
 * Hibernate only needs the id to persist the foreign key, it doesn't need
 * the full related row loaded.
 */
public final class RecordDtoMapper {

    private RecordDtoMapper() {
    }

    public static Record toEntity(RecordCreateRequest request) {
        Record record = new Record();
        applyFields(record, request.enrollmentId(), request.employeeId(), request.offenseId(),
                request.dateOfViolation(), request.actionId(), request.remarks());
        return record;
    }

    public static Record toEntity(RecordUpdateRequest request) {
        Record record = new Record();
        record.setRecordId(request.recordId());
        applyFields(record, request.enrollmentId(), request.employeeId(), request.offenseId(),
                request.dateOfViolation(), request.actionId(), request.remarks());
        return record;
    }

    private static void applyFields(
            Record record,
            long enrollmentId,
            String employeeId,
            long offenseId,
            LocalDate dateOfViolation,
            long actionId,
            String remarks) {

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        record.setEnrollment(enrollment);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        record.setEmployee(employee);

        Offense offense = new Offense();
        offense.setOffenseId(offenseId);
        record.setOffense(offense);

        DisciplinaryAction action = new DisciplinaryAction();
        action.setActionId(actionId);
        record.setAction(action);

        record.setDateOfViolation(DateConversion.toDate(dateOfViolation));
        record.setRemarks(remarks);
    }

    public static RecordResponse toResponse(Record record) {
        if (record == null) {
            return null;
        }
        return new RecordResponse(
                record.getRecordId(),
                CommonDtoMapper.toEnrollmentSummary(record.getEnrollment()),
                CommonDtoMapper.toEmployeeSummary(record.getEmployee()),
                CommonDtoMapper.toOffenseSummary(record.getOffense()),
                DateConversion.toLocalDate(record.getDateOfViolation()),
                CommonDtoMapper.toActionSummary(record.getAction()),
                DateConversion.toLocalDate(record.getDateOfResolution()),
                record.getRemarks(),
                record.getStatus());
    }
}
