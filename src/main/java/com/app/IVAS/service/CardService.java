package com.app.IVAS.service;

import com.app.IVAS.Enum.RegType;
import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.dto.CardDto;
import com.app.IVAS.dto.PrintDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import org.springframework.core.io.Resource;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface CardService {

    CardDetailsDto getCardDetails(String invoiceNumber);

    Card createCard(@NonNull Invoice invoice, @NonNull Vehicle vehicle, RegType regType);

    List<CardDto> get(List<Card> cards);

    Resource printCard(List<PrintDto> dtos) throws Exception;
    
    List<Card> updateCardByPayment(@NonNull String invoiceNumber, @NonNull Double amount);

    Resource printDocuments(List<PrintDto> dtos) throws Exception;
}
