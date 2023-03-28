package com.app.IVAS.repository;

import com.app.IVAS.entity.WorkFLowLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowLogRepository extends JpaRepository<WorkFLowLog, Long> {
}
