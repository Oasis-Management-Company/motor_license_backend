package com.app.IVAS.repository;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceServiceTypeRepository extends JpaRepository<InvoiceServiceType, Long> {
    List<InvoiceServiceType> findByInvoice(Invoice invoice);
}
