package org.rocs.osdrmsa.controller.common.mapper;

import org.rocs.osdrmsa.controller.common.dto.ActionSummary;
import org.rocs.osdrmsa.controller.common.dto.EmployeeSummary;
import org.rocs.osdrmsa.controller.common.dto.EnrollmentSummary;
import org.rocs.osdrmsa.controller.common.dto.OffenseSummary;
import org.rocs.osdrmsa.controller.common.dto.RecordSummary;
import org.rocs.osdrmsa.controller.common.dto.StudentSummary;
import org.rocs.osdrmsa.domain.disciplinary.action.DisciplinaryAction;
import org.rocs.osdrmsa.domain.enrollment.Enrollment;
import org.rocs.osdrmsa.domain.offense.Offense;
import org.rocs.osdrmsa.domain.person.Person;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.person.student.Student;
import org.rocs.osdrmsa.domain.record.Record;

/**
 * Shared entity-to-summary-DTO conversions used across Record/Appeal/Request
 * responses. These entity associations are all EAGER-fetched by default
 * (@ManyToOne/@OneToOne), so they're safe to read here even though this
 * runs after the originating service call/transaction has returned.
 */
public final class CommonDtoMapper {

    private CommonDtoMapper() {
    }

    public static StudentSummary toStudentSummary(Student student) {
        if (student == null) {
            return null;
        }
        return new StudentSummary(student.getStudentId(), fullName(student.getPerson()));
    }

    public static EmployeeSummary toEmployeeSummary(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeSummary(
                employee.getEmployeeId(), fullName(employee.getPerson()), employee.getEmployeeRole());
    }

    public static EnrollmentSummary toEnrollmentSummary(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }
        return new EnrollmentSummary(
                enrollment.getEnrollmentId(),
                toStudentSummary(enrollment.getStudent()),
                enrollment.getSchoolYear(),
                enrollment.getStudentLevel(),
                enrollment.getSection(),
                enrollment.getDepartment());
    }

    public static OffenseSummary toOffenseSummary(Offense offense) {
        if (offense == null) {
            return null;
        }
        return new OffenseSummary(offense.getOffenseId(), offense.getOffense(), offense.getType());
    }

    public static ActionSummary toActionSummary(DisciplinaryAction action) {
        if (action == null) {
            return null;
        }
        return new ActionSummary(action.getActionId(), action.getActionName());
    }

    public static RecordSummary toRecordSummary(Record record) {
        if (record == null) {
            return null;
        }
        return new RecordSummary(record.getRecordId(), toOffenseSummary(record.getOffense()), record.getStatus());
    }

    private static String fullName(Person person) {
        if (person == null) {
            return null;
        }
        StringBuilder name = new StringBuilder();
        appendPart(name, person.getFirstName());
        appendPart(name, person.getMiddleName());
        appendPart(name, person.getLastName());
        return name.toString();
    }

    private static void appendPart(StringBuilder builder, String part) {
        if (part == null || part.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(part);
    }
}
