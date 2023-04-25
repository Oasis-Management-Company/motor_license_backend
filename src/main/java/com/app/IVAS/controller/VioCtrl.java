package com.app.IVAS.controller;


import com.app.IVAS.Enum.RegType;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.PortalUserPojo;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.dto.filters.PortalUserSearchFilter;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.filter.SalesSearchFilter;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VioService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vio")
public class VioCtrl {

    @Autowired
    private VioService vioService;
    private final JwtService jwtService;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;

    @GetMapping("/get/all/offenses")
    public ResponseEntity<List<Offense>> getAllOffenses(){
        List<Offense> offenses = vioService.getAllOffenses();
        return ResponseEntity.ok(offenses);
    }

    @PostMapping("/save/all/offense-type/phone-number")
    public ResponseEntity<?> saveOffenseTypeByPhonenumber(@RequestParam String phoneNumber, @RequestParam List<Long> ids){
        Invoice dto = vioService.saveOffenseTypeByPhonenumber(phoneNumber, ids);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/get/offenseType")
    public ResponseEntity<List<InvoiceOffenseType>> getOffenseTypeByInvoice(@RequestParam Long salesId){
        return ResponseEntity.ok(vioService.getOffenseTypeByInvoice(salesId));
    }

    @PostMapping("/get/offenseType/payer")
    public ResponseEntity<PortalUser> getOffensePayer(@RequestParam String phoneNumber){
        return ResponseEntity.ok(vioService.getOffensePayer(phoneNumber));
    }

    @PostMapping("/get/offense/assessment")
    public QueryResults<SalesDto> searchAllOffeseForVIO(SalesSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.plateType.eq(RegType.OFFENCE))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getAfter()!= null && !filter.getAfter().equals("")) {
            LocalDate startDate =  LocalDate.parse(filter.getAfter(), formatter);
            userJPAQuery.where(QSales.sales.createdAt.goe(startDate.atStartOfDay()));
        }
        if (filter.getBefore() != null && !filter.getBefore().equals("")) {
            LocalDate endDate = LocalDate.parse(filter.getBefore(), formatter);
            userJPAQuery.where(QSales.sales.createdAt.loe(endDate.atTime(LocalTime.MAX)));

        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QSales.sales);
        QueryResults<Sales> userQueryResults = userJPAQuery.select(QSales.sales).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vioService.searchAllForVIO(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

}