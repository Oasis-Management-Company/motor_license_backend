package com.app.IVAS.repository;

import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.entity.WorkFlowStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStageRepository extends JpaRepository<WorkFlowStage, Long> {

    WorkFlowStage findByTypeAndStep(WorkFlowType type, Long step);
}
