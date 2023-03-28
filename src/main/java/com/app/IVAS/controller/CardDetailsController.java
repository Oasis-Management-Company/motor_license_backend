package com.app.IVAS.controller;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.service.CardDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card-details")
public class CardDetailsController {

    private final CardDetailsService cardDetailsService;


    @GetMapping("/search/{invoiceNumber}")
    public ResponseEntity<CardDetailsDto> getCardDetails(@PathVariable String invoiceNumber){

        return ResponseEntity.ok(cardDetailsService.getCardDetails(invoiceNumber));
    }
}
