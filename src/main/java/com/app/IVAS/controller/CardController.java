package com.app.IVAS.controller;

import com.app.IVAS.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;


    @GetMapping("/get_full_details/{invoiceNumber}")
    public ResponseEntity<?> getCardDetails(@PathVariable String invoiceNumber){

        return ResponseEntity.ok(cardService.getCardDetails(invoiceNumber));
    }

    @PostMapping("/update_by_payment/{invoiceNumber}/{amount}")
    public ResponseEntity<?> updateCardByPayment(@PathVariable String invoiceNumber, @PathVariable Double amount){

        return ResponseEntity.ok(cardService.updateCardByPayment(invoiceNumber, amount));
    }


}
