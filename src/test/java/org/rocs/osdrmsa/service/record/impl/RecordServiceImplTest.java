package org.rocs.osdrmsa.service.record.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osdrmsa.domain.person.employee.Employee;
import org.rocs.osdrmsa.domain.record.Record;
import org.rocs.osdrmsa.domain.record.RecordStatus;
import org.rocs.osdrmsa.repository.record.RecordRepository;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    private RecordRepository recordRepository;

    @InjectMocks
    private RecordServiceImpl recordService;

    private Record validRecord;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setEmployeeId("EMP-001");

        validRecord = new Record();
        validRecord.setEmployee(employee);
        validRecord.setDateOfViolation(new Date());
    }

    @Test
    void createStudentRecord_rejectsNullRecord() {
        assertThat(recordService.createStudentRecord(null)).isNull();
    }

    @Test
    void createStudentRecord_rejectsMissingEmployee() {
        Record record = new Record();
        record.setDateOfViolation(new Date());

        assertThat(recordService.createStudentRecord(record)).isNull();
    }

    @Test
    void createStudentRecord_rejectsMissingDateOfViolation() {
        Employee employee = new Employee();
        employee.setEmployeeId("EMP-001");

        Record record = new Record();
        record.setEmployee(employee);

        assertThat(recordService.createStudentRecord(record)).isNull();
    }

    @Test
    void createStudentRecord_rejectsOverlongRemarks() {
        validRecord.setRemarks("x".repeat(501));

        assertThat(recordService.createStudentRecord(validRecord)).isNull();
    }

    @Test
    void createStudentRecord_setsStatusPendingOnSuccess() {
        when(recordRepository.save(any(Record.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Record saved = recordService.createStudentRecord(validRecord);

        assertThat(saved).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(RecordStatus.PENDING);
    }

    @Test
    void resolveRecord_returnsNullWhenNotFound() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(recordService.resolveRecord(99L)).isNull();
    }

    @Test
    void resolveRecord_setsResolvedStatusAndDate() {
        validRecord.setRecordId(1L);
        validRecord.setStatus(RecordStatus.PENDING);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(validRecord));
        when(recordRepository.save(any(Record.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Record resolved = recordService.resolveRecord(1L);

        assertThat(resolved.getStatus()).isEqualTo(RecordStatus.RESOLVED);
        assertThat(resolved.getDateOfResolution()).isNotNull();
    }
}
