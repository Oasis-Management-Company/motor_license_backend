package com.app.IVAS.repository;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumberIgnoreCase(String invoiceNumber);

    List<Invoice> findByPayer(PortalUser user);

    List<Invoice> findByVehicle(Vehicle vehicle);
}
