package com.app.IVAS.repository;


import com.app.IVAS.entity.PreferredPlate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferredPlateRepository extends JpaRepository<PreferredPlate, Long> {
}
