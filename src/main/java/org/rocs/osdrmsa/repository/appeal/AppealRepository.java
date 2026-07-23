package org.rocs.osdrmsa.repository.appeal;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, Long> {

    List<Appeal> findByEnrollmentStudentStudentId(String studentId);

    List<Appeal> findByRecordRecordId(Long recordId);
}
