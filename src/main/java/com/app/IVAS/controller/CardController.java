package com.app.IVAS.controller;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.CardDto;
import com.app.IVAS.dto.PrintDto;
import com.app.IVAS.dto.filters.CardSearchFilter;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.QCard;
import com.app.IVAS.entity.userManagement.ZonalOffice;
import com.app.IVAS.repository.ZonalOfficeRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.CardService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ZonalOfficeRepository zonalOfficeRepository;


    @GetMapping("/get_full_details/{invoiceNumber}")
    public ResponseEntity<?> getCardDetails(@PathVariable String invoiceNumber){

        return ResponseEntity.ok(cardService.getCardDetails(invoiceNumber));
    }

    @PostMapping("/update_cards_by_payment/{invoiceNumber}/{amount}")
    public ResponseEntity<?> updateCardByPayment(@PathVariable String invoiceNumber, @PathVariable Double amount){

        return ResponseEntity.ok(cardService.updateCardByPayment(invoiceNumber, amount));
    }

    @GetMapping("/list/zones")
    public ResponseEntity<List<ZonalOffice>> getZoneList(){

        List<ZonalOffice> zonalOffices = zonalOfficeRepository.findAll();
        Collections.sort(zonalOffices, Comparator.comparing(ZonalOffice::getName));

        return ResponseEntity.ok(zonalOffices);
    }

    @GetMapping("/search")
    @Transactional
    public QueryResults<CardDto> searchCards(CardSearchFilter filter){

        JPAQuery<Card> cardJPAQuery = appRepository.startJPAQuery(QCard.card)
                .where(predicateExtractor.getPredicate(filter))
                .where(QCard.card.cardStatus.ne(CardStatusConstant.NOT_PAID))
                .where(QCard.card.status.eq(GenericStatusConstant.ACTIVE))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            cardJPAQuery.where(QCard.card.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            cardJPAQuery.where(QCard.card.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QCard.card);
        QueryResults<Card> cardQueryResults = cardJPAQuery.select(QCard.card).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(cardService.get(cardQueryResults.getResults()), cardQueryResults.getLimit(), cardQueryResults.getOffset(), cardQueryResults.getTotal());
    }

    @PostMapping("/card/print")
    @Transactional
    public ResponseEntity<Resource> printCard(@RequestBody List<PrintDto> dtos, HttpServletRequest request) throws Exception {
        System.out.println(dtos);
        Resource resource = cardService.printCard(dtos);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/pdf";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=card.pdf")
                .body(resource);
    }

    @PostMapping("/document/print")
    @Transactional
    public ResponseEntity<Resource> printDocuments(@RequestBody List<PrintDto> dtos, HttpServletRequest request) throws Exception {
        System.out.println(dtos);
        Resource resource = cardService.printDocuments(dtos);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/pdf";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=card.pdf")
                .body(resource);
    }


}
