package com.app.IVAS.controller;

import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.PlateNumberRequestDto;
import com.app.IVAS.dto.PlateNumberRequestPojo;
import com.app.IVAS.dto.filters.PlateNumberRequestSearchFilter;
import com.app.IVAS.entity.PlateNumberRequest;
import com.app.IVAS.entity.QPlateNumberRequest;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.RequestService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/request")
public class RequestController {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final RequestService requestService;

    @GetMapping("/search/plate-number-request")
    @Transactional
    public QueryResults<PlateNumberRequestPojo> searchPlateNumberRequest(PlateNumberRequestSearchFilter filter){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        JPAQuery<PlateNumberRequest> plateNumberRequestJPAQuery = appRepository.startJPAQuery(QPlateNumberRequest.plateNumberRequest)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            plateNumberRequestJPAQuery.where(QPlateNumberRequest.plateNumberRequest.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            plateNumberRequestJPAQuery.where(QPlateNumberRequest.plateNumberRequest.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPlateNumberRequest.plateNumberRequest);
        QueryResults<PlateNumberRequest> plateNumberRequestQueryResults = plateNumberRequestJPAQuery.select(QPlateNumberRequest.plateNumberRequest).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(requestService.getPlateNumberRequest(plateNumberRequestQueryResults.getResults()), plateNumberRequestQueryResults.getLimit(), plateNumberRequestQueryResults.getOffset(), plateNumberRequestQueryResults.getTotal());
    }

    @PostMapping("/create/plate-number-request")
    @Transactional
    public ResponseEntity<?> createPlateNumberRequest(@RequestBody PlateNumberRequestDto dto){
        requestService.CreatePlateNumberRequest(dto);
        return ResponseEntity.ok("Plate Number Request was created successfully");
    }

//    @PostMapping("/update/plate-number-request")
//    @Transactional
//    public
}
