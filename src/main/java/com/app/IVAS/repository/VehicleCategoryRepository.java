package com.app.IVAS.repository;

import com.app.IVAS.entity.VehicleCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Long> {


    List<VehicleCategory> findAllByOrderByNameAsc();
}
