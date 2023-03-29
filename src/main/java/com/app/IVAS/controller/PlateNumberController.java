package com.app.IVAS.controller;


import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.dto.PlateNumberPojo;
import com.app.IVAS.dto.StockPojo;
import com.app.IVAS.dto.filters.PlateNumberSearchFilter;
import com.app.IVAS.dto.filters.StockSearchFilter;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.repository.PlateNumberSubTypeRepository;
import com.app.IVAS.repository.PlateNumberTypeRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.PlateNumberService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plate-number")
public class PlateNumberController {

    private final PlateNumberService plateNumberService;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final PlateNumberTypeRepository plateNumberTypeRepository;
    private final PlateNumberSubTypeRepository plateNumberSubTypeRepository;
    private final JwtService jwtService;

    @GetMapping("/search/stock")
    @Transactional
    public QueryResults<StockPojo> searchStock(StockSearchFilter filter){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JPAQuery<Stock> stockJPAQuery = appRepository.startJPAQuery(QStock.stock)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            stockJPAQuery.where(QStock.stock.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            stockJPAQuery.where(QStock.stock.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QStock.stock);
        QueryResults<Stock> stockQueryResults = stockJPAQuery.select(QStock.stock).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(plateNumberService.getStock(stockQueryResults.getResults()), stockQueryResults.getLimit(), stockQueryResults.getOffset(), stockQueryResults.getTotal());
    }

    @GetMapping("/search/plate-number")
    @Transactional
    public QueryResults<PlateNumberPojo> searchPlateNumber(PlateNumberSearchFilter filter){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JPAQuery<PlateNumber> plateNumberJPAQuery = appRepository.startJPAQuery(QPlateNumber.plateNumber1)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.isAgent()){
            plateNumberJPAQuery.where(QPlateNumber.plateNumber1.agent.eq(jwtService.user));
        }

        if (filter.getCreatedAfter() != null){
            plateNumberJPAQuery.where(QPlateNumber.plateNumber1.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            plateNumberJPAQuery.where(QPlateNumber.plateNumber1.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPlateNumber.plateNumber1);
        QueryResults<PlateNumber> plateNumberQueryResults = plateNumberJPAQuery.select(QPlateNumber.plateNumber1).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(plateNumberService.getPlateNumbers(plateNumberQueryResults.getResults()), plateNumberQueryResults.getLimit(), plateNumberQueryResults.getOffset(), plateNumberQueryResults.getTotal());
    }

    @PostMapping("/upload-stock")
    public ResponseEntity<Map<String,Object>> uploadStock(@RequestBody PlateNumberDto dto){ ;
       return ResponseEntity.ok(plateNumberService.createStock(dto));
    }

    @GetMapping("/get-plate-number-type")
    public List<PlateNumberType> getPlateNumberTypes(){
        return plateNumberTypeRepository.findAll();
    }

    @GetMapping("/get-plate-number-sub")
    public List<PlateNumberSubType> getPlateNumberSub(@RequestParam Long id){
        PlateNumberType type = plateNumberTypeRepository.findById(id).get();
        return plateNumberSubTypeRepository.findByType(type);
    }

    @PostMapping("/assign-plate-number")
    @Transactional
    public ResponseEntity<?> assignPlateNumber(@RequestParam List<Long> plateNumbers,
                                               @RequestParam Long mlaId,
                                               @RequestParam Long requestId){

        plateNumberService.assignPlateNumbers(plateNumbers, mlaId, requestId);
        return ResponseEntity.ok("");
    }
}
