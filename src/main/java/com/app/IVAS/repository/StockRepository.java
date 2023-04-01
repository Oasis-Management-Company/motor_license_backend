package com.app.IVAS.repository;

import com.app.IVAS.entity.Stock;
import com.app.IVAS.entity.userManagement.Lga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
