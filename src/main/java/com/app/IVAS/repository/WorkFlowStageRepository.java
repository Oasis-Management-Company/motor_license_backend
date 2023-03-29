package com.app.IVAS.repository;

import com.app.IVAS.Enum.WorkFlowType;
import com.app.IVAS.entity.WorkFlowStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkFlowStageRepository extends JpaRepository<WorkFlowStage, Long> {

    Optional<WorkFlowStage> findByTypeAndStep(WorkFlowType type, Long step);

    List<WorkFlowStage> findByType(WorkFlowType type);
}
