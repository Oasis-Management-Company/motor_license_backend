package com.app.IVAS.repository;

import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.PlateNumberType;
import com.app.IVAS.entity.Stock;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
    List<PlateNumber> findAllByAgentAndPlateNumberStatusAndTypeAndOwnerIsNull(PortalUser user, PlateNumberStatus assigned, PlateNumberType type);

    List<PlateNumber> findByTypeAndPlateNumberStatus(PlateNumberType type, PlateNumberStatus status);

    List<PlateNumber> findByStockAndPlateNumberStatus(Stock stock, PlateNumberStatus status);

    PlateNumber findFirstByPlateNumberIgnoreCase(String plate);

    List<PlateNumber> findByRequest(PlateNumberRequest request);

    List<PlateNumber> findByAgentAndPlateNumberStatus(PortalUser user, PlateNumberStatus status);

    List<PlateNumber> findByPlateNumberStatus(PlateNumberStatus status);

    Optional<PlateNumber> findPlateNumberByPlateNumberIgnoreCase(String plateNumber);
}
