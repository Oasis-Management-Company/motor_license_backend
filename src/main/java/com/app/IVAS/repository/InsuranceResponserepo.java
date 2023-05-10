package com.app.IVAS.repository;

import com.app.IVAS.entity.InsuranceResponse;
import com.app.IVAS.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceResponserepo extends JpaRepository<InsuranceResponse, Long> {
    InsuranceResponse findFirstByVehicle(Vehicle vehicle);
}
