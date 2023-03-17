package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumberSubType;
import com.app.IVAS.entity.PlateNumberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlateNumberSubTypeRepository extends JpaRepository<PlateNumberSubType, Long> {

    PlateNumberSubType findByName(String name);

    List<PlateNumberSubType> findByType(PlateNumberType type);
}
