package com.app.IVAS.repository;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.PlateNumberType;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    List<ServiceType> findAllByCategory(VehicleCategory category);
    
     Optional<ServiceType> findByName(String name);

    List<ServiceType> findAllByCategoryAndPlateNumberType(VehicleCategory category, PlateNumberType type);

    List<ServiceType> findAllByCategoryAndPlateNumberTypeAndRegType(VehicleCategory category, PlateNumberType types, RegType registration);

    List<ServiceType> findAllByCategoryAndRegTypeAndPlateNumberTypeOrCategoryIsNotNullAndPlateNumberTypeIsNull(VehicleCategory vehicleCategory, RegType renewal, PlateNumberType type);

    List<ServiceType> findAllByCategoryAndRegTypeAndPlateNumberTypeOrCategoryAndPlateNumberTypeIsNull(VehicleCategory vehicleCategory, RegType renewal, PlateNumberType type, VehicleCategory vehicleCategory1);

    List<ServiceType> findAllByCategoryIsNullAndPlateNumberTypeIsNull();

}
