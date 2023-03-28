package com.app.IVAS.repository;

import com.app.IVAS.entity.OtherServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherServiceTypeRepository extends JpaRepository<OtherServiceType, Long> {
}
