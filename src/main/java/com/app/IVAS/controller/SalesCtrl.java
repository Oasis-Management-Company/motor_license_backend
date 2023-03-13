package com.app.IVAS.controller;

import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.filter.SalesSearchFilter;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.response.JsonResponse;
import com.app.IVAS.service.SalesCtrlService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api/")
public class SalesCtrl {
    private SalesCtrlService service;

    private final AppRepository appRepository;

    private final PredicateExtractor predicateExtractor;

    @GetMapping(name = "/save/sales")
    public ResponseEntity<?> SaveSales(@RequestBody SalesDto sales){
        return ResponseEntity.ok(new JsonResponse("Successful", service.SaveSales(sales)));
    }

    @PostMapping("/get/sales")
    public QueryResults<SalesDto> searchDirectTax(SalesSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


//        if (filter.getAfter()!= null && !filter.getAfter().equals("")) {
//            LocalDate startDate =  LocalDate.parse(filter.getAfter(), formatter);
//            userJPAQuery.where(QSales.sales.approvalDate.goe(startDate.atStartOfDay()));
//        }
//        if (filter.getBefore() != null && !filter.getBefore().equals("")) {
//            LocalDate endDate = LocalDate.parse(filter.getBefore(), formatter);
//            userJPAQuery.where(QSales.sales.approvalDate.loe(endDate.atTime(LocalTime.MAX)));
//
//        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("approvalDate"), QSales.sales);
        QueryResults<Sales> userQueryResults = userJPAQuery.select(QSales.sales).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(service.GetSales(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

}
