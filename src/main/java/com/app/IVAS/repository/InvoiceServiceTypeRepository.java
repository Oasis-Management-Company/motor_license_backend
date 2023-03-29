package com.app.IVAS.repository;

import com.app.IVAS.entity.InvoiceServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceServiceTypeRepository extends JpaRepository<InvoiceServiceType, Long> {
}
