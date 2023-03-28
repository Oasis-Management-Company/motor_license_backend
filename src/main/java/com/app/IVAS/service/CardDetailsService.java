package com.app.IVAS.service;

import com.app.IVAS.dto.CardDetailsDto;

import java.util.Optional;

public interface CardDetailsService {

    CardDetailsDto getCardDetails(String invoiceNumber);
}
