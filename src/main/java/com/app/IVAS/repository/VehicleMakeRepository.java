package com.app.IVAS.repository;

import com.app.IVAS.entity.VehicleMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleMakeRepository extends JpaRepository<VehicleMake, Long> {

    Optional<VehicleMake> findByNameIgnoreCase(String name);


    VehicleMake findFirstByNameIgnoreCase(String make);
}
