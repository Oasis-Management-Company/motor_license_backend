package com.app.IVAS.repository;

import com.app.IVAS.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByInvoiceInvoiceNumberIgnoreCase(String invoiceNumber);

}
