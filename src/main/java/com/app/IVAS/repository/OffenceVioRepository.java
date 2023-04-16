package com.app.IVAS.repository;

import com.app.IVAS.entity.OffenceVIO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffenceVioRepository extends JpaRepository<OffenceVIO, Long> {
}
