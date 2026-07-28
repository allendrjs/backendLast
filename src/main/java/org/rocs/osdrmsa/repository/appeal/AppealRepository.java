package org.rocs.osdrmsa.repository.appeal;

import org.rocs.osdrmsa.domain.appeal.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, Long> {

    List<Appeal> findByStatus(String status);

    List<Appeal> findByRecord_RecordId(Long recordId);

    List<Appeal> findByEnrollment_Student_StudentId(String studentId);

}
