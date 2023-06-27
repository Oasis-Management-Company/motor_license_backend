package com.app.IVAS.repository;

import com.app.IVAS.entity.LegacyAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyAssessmentRepo extends JpaRepository<LegacyAssessment, Long> {
}
