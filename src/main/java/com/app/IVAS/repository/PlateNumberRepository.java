package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
}
