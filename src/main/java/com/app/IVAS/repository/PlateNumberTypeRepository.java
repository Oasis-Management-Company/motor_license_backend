package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNumberTypeRepository extends JpaRepository<PlateNumberType, Long> {

    PlateNumberType findByName(String name);
}
