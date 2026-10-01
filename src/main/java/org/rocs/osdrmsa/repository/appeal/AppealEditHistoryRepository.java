package org.rocs.osdrmsa.repository.appeal;

import org.rocs.osdrmsa.domain.appeal.AppealEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppealEditHistoryRepository extends JpaRepository<AppealEditHistory, Long> {

    List<AppealEditHistory> findByAppeal_AppealIdOrderByEditedAtAsc(Long appealId);
}
