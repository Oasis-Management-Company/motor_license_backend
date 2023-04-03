package com.app.IVAS.repository;

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

    Vehicle findByChasisNumberIgnoreCase(String chassis);

    List<Vehicle> findByPortalUser(PortalUser user);

    Vehicle findFirstByPlateNumber(PlateNumber plateNumber);
}
