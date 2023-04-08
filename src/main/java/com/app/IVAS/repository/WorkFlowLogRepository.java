package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.WorkFLowLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkFlowLogRepository extends JpaRepository<WorkFLowLog, Long> {

    List<WorkFLowLog> findByRequest(PlateNumberRequest request);
}
