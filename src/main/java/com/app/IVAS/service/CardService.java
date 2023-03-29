package com.app.IVAS.service;

import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CardService {

    CardDetailsDto getCardDetails(String invoiceNumber);

    Card createCard(@NonNull Invoice invoice, @NonNull Vehicle vehicle);

    List<Card> updateCardByPayment(@NonNull String invoiceNumber, @NonNull Double amount);
}
