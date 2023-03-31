package com.app.IVAS.repository;

import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.PlateNumberType;
import com.app.IVAS.entity.Stock;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
    List<PlateNumber> findAllByAgentAndPlateNumberStatusAndTypeAndOwnerIsNull(PortalUser user, PlateNumberStatus assigned, PlateNumberType type);

    List<PlateNumber> findByTypeAndPlateNumberStatus(PlateNumberType type, PlateNumberStatus status);

    List<PlateNumber> findByStockAndPlateNumberStatus(Stock stock, PlateNumberStatus status);
}
