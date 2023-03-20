package com.app.IVAS.repository;


import com.app.IVAS.entity.userManagement.Area;
import com.app.IVAS.entity.userManagement.Lga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByLga(Lga lga);

}
