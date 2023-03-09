package com.app.IVAS.repository;

import com.app.IVAS.entity.userManagement.Lga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LgaRepository extends JpaRepository<Lga, Long> {

    @Query(value="SELECT * FROM lga p  where p.state_id = 6  ORDER BY name ASC ", nativeQuery = true)
    List<Lga> findAllByStateId();
}
