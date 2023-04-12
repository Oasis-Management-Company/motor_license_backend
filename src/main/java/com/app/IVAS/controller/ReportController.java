package com.app.IVAS.controller;


import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.*;
import com.app.IVAS.dto.filters.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QStock;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.repository.PlateNumberRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.ReportService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final JwtService jwtService;
    private final ReportService reportService;
    private final PlateNumberRepository plateNumberRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @GetMapping("/search/plate-number-sales")
    @Transactional
    public QueryResultsPojo<SalesReportDto> searchPlateNumberSales(SalesReportSearchFilter filter){

        JPAQuery<Sales> salesJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.invoice.paymentDate.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<Sales> salesListJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.invoice.paymentDate.isNotNull());

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            salesJPAQuery.where(QSales.sales.createdBy.eq(jwtService.user));
            salesListJPAQuery.where(QSales.sales.createdBy.eq(jwtService.user));
        }

        if (filter.getCreatedAfter() != null){
            salesJPAQuery.where(QSales.sales.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
            salesListJPAQuery.where(QSales.sales.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            salesJPAQuery.where(QSales.sales.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
            salesListJPAQuery.where(QSales.sales.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        List<Double> prices = new ArrayList<>();

        for (Sales sales:salesListJPAQuery.fetch()){
            prices.add(appRepository.startJPAQuery(com.app.IVAS.entity.QInvoiceServiceType.invoiceServiceType)
                    .where(QInvoiceServiceType.invoiceServiceType.invoice.eq(sales.getInvoice()).and(QInvoiceServiceType.invoiceServiceType.serviceType.name.equalsIgnoreCase("PLATE NUMBER REGISTRATION")))
                    .fetchFirst().getServiceType().getPrice());
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QSales.sales);
        QueryResults<Sales> salesQueryResults = salesJPAQuery.select(QSales.sales).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getSales(salesQueryResults.getResults()), salesQueryResults.getLimit(), salesQueryResults.getOffset(), salesQueryResults.getTotal(), salesQueryResults.isEmpty(), null, prices.stream().mapToDouble(Double::doubleValue).sum());
    }

    @GetMapping("/search/stock-level-report")
    @Transactional
    public QueryResultsPojo<StockReportPojo> searchStockReport(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("MLA"))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        Map<String, Object> meta = new HashMap<>();

        meta.put("totalStock", plateNumberRepository.findAll().size());
        meta.put("assignedStock", plateNumberRepository.findByPlateNumberStatus(PlateNumberStatus.ASSIGNED).size());
        meta.put("unassignedStock", plateNumberRepository.findByPlateNumberStatus(PlateNumberStatus.UNASSIGNED).size());
        meta.put("soldStock", plateNumberRepository.findByPlateNumberStatus(PlateNumberStatus.SOLD).size());

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getStockReport(portalUserQueryResults.getResults()), portalUserQueryResults.getLimit(), portalUserQueryResults.getOffset(), portalUserQueryResults.getTotal(), portalUserQueryResults.isEmpty(), meta, null);
    }

    @GetMapping("/search/plate-number-assignment")
    @Transactional
    public QueryResults<AssignedReportPojo> searchAssignedPlateNumber(PlateNumberSearchFilter filter){

        JPAQuery<PlateNumber> plateNumberJPAQuery = appRepository.startJPAQuery(QPlateNumber.plateNumber1)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPlateNumber.plateNumber1.plateNumberStatus.ne(PlateNumberStatus.UNASSIGNED))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        if (filter.getCreatedAfter() != null){
            plateNumberJPAQuery.where(QPlateNumber.plateNumber1.request.lastUpdatedAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            plateNumberJPAQuery.where(QPlateNumber.plateNumber1.request.lastUpdatedAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPlateNumber.plateNumber1);
        QueryResults<PlateNumber> plateNumberQueryResults = plateNumberJPAQuery.select(QPlateNumber.plateNumber1).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(reportService.getAssignedPlateNumbers(plateNumberQueryResults.getResults()), plateNumberQueryResults.getLimit(), plateNumberQueryResults.getOffset(), plateNumberQueryResults.getTotal());
    }
}
