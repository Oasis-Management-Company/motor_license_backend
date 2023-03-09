package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumberSubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNumberSubTypeRepository extends JpaRepository<PlateNumberSubType, Long> {

    PlateNumberSubType findByName(String name);
}
