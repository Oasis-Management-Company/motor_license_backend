package com.app.IVAS.repository;

import com.app.IVAS.entity.PlateNumberRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateNumberRequestRepository extends JpaRepository<PlateNumberRequest, Long> {

    PlateNumberRequest findByTrackingId(String trackingId);
}
