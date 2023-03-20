package com.app.IVAS.repository;

import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
    List<VehicleModel> findAllByVehicleMake(VehicleMake make);
}
