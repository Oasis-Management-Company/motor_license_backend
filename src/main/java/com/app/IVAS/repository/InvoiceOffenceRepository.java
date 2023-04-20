package com.app.IVAS.repository;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceOffenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceOffenceRepository extends JpaRepository<InvoiceOffenseType, Long> {
    List<InvoiceOffenseType> findByInvoice(Invoice invoice);
}
