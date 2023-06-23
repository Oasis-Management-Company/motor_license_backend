package com.app.IVAS.repository;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findFirstByChasisNumber(String chasis);

    Vehicle findByChasisNumber(String chasis);

    Vehicle findByChasisNumberAndRegTypeIsNot(String chasis, RegType edit);

    Vehicle findByChasisNumberIgnoreCase(String chassis);

    List<Vehicle> findByPortalUser(PortalUser user);

    List<Vehicle> findAllByPortalUserAndRegTypeIsNot(PortalUser user, RegType edit);

    Vehicle findFirstByPlateNumber(PlateNumber plateNumber);

    Vehicle findByPlateNumberAndRegTypeIsNot(PlateNumber plateNumber, RegType edit);

    Vehicle findVehicleByPlateNumberId(Long id);

    Vehicle findVehicleByPlateNumberIdAndRegType(Long id, RegType regType);

    Vehicle findFirstByParentId(Long id);

    Vehicle findFirstByPortalUser(PortalUser payer);

    Vehicle findByPortalUserAndRegTypeIsNot(PortalUser payer, RegType edit);
}
