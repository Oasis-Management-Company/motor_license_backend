package com.app.IVAS.repository;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceServiceTypeRepository extends JpaRepository<InvoiceServiceType, Long> {

    Optional<List<InvoiceServiceType>> findAllByInvoice(Invoice invoice);

    List<InvoiceServiceType> findByInvoice(Invoice invoice);

}
