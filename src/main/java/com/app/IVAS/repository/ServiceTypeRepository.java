package com.app.IVAS.repository;

import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    List<ServiceType> findAllByCategory(VehicleCategory category);
}
