package com.app.IVAS.repository;

import com.app.IVAS.entity.Offense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffenseRepository extends JpaRepository<Offense, Long> {
}
