package com.app.IVAS.repository;

import com.app.IVAS.entity.ZonalOffice;
import com.app.IVAS.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonalOfficeRepository extends JpaRepository<ZonalOffice, Long> {

    List<ZonalOffice> findByZone(Zone zone);
}
