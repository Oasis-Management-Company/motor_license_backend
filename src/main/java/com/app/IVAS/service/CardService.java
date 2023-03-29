package com.app.IVAS.service;

import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface CardService {

    CardDetailsDto getCardDetails(String invoiceNumber);

    Card createCard(Invoice invoice, Vehicle vehicle);

    List<Card> updateCardByPayment(String invoiceNumber, Double amount);
}
