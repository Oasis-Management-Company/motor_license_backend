package com.app.IVAS.controller;


import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.*;
import com.app.IVAS.dto.filters.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QServiceType;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @GetMapping("/search/plate-number-sales")
    @Transactional
    public QueryResultsPojo<SalesReportDto> searchPlateNumberSales(SalesReportSearchFilter filter){

        JPAQuery<Sales> salesJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.invoice.paymentDate.isNotNull())
                .where(QSales.sales.vehicle.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<Sales> salesListJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.vehicle.isNotNull())
                .where(QSales.sales.invoice.paymentDate.isNotNull());

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            salesJPAQuery.where(QSales.sales.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
            salesListJPAQuery.where(QSales.sales.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
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
            InvoiceServiceType invoiceService = appRepository.startJPAQuery(com.app.IVAS.entity.QInvoiceServiceType.invoiceServiceType)
                    .where(QInvoiceServiceType.invoiceServiceType.invoice.eq(sales.getInvoice()).and(QInvoiceServiceType.invoiceServiceType.serviceType.name.equalsIgnoreCase("PLATE NUMBER REGISTRATION")))
                    .fetchFirst();
            if (invoiceService != null){
                prices.add(invoiceService.getServiceType().getPrice());
            }
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QSales.sales);
        QueryResults<Sales> salesQueryResults = salesJPAQuery.select(QSales.sales).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getSales(salesQueryResults.getResults()), salesQueryResults.getLimit(), salesQueryResults.getOffset(), (long) prices.size(), salesQueryResults.isEmpty(), null, prices.stream().mapToDouble(Double::doubleValue).sum());
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

        int totalStock = appRepository.startJPAQuery(com.app.IVAS.entity.QPlateNumber.plateNumber1)
                .where(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.ASSIGNED)
                        .or(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.UNASSIGNED)))
                .fetch().size();

        int unassignedStock = appRepository.startJPAQuery(com.app.IVAS.entity.QPlateNumber.plateNumber1)
                .where(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.UNASSIGNED))
                .fetch().size();

        int soldStock = appRepository.startJPAQuery(com.app.IVAS.entity.QPlateNumber.plateNumber1)
                .where(QPlateNumber.plateNumber1.request.isNotNull())
                .where(QPlateNumber.plateNumber1.plateNumberStatus.eq(PlateNumberStatus.SOLD))
                .fetch().size();

        meta.put("totalStock", totalStock);
        meta.put("unassignedStock", unassignedStock);
        meta.put("soldStock", soldStock);
        meta.put("assignedStock", totalStock - unassignedStock);

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
                .where(QPlateNumber.plateNumber1.request.isNotNull())
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

    @GetMapping("/search/service-type-sales")
    @Transactional
    public QueryResultsPojo<SalesReportPojo> searchServiceSales(SalesReportSearchFilter filter){

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notEqualsIgnoreCase("PLATE NUMBER REGISTRATION"))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<InvoiceServiceType> typeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notEqualsIgnoreCase("PLATE NUMBER REGISTRATION"))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull());

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
        }

        if (filter.getCreatedAfter() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        List<Double> prices = new ArrayList<>();
        for(InvoiceServiceType serviceType:typeJPAQuery.fetch()){
            prices.add(serviceType.getAmount());
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceServiceType.invoiceServiceType);
        QueryResults<InvoiceServiceType> invoiceServiceTypeQueryResults = invoiceServiceTypeJPAQuery.select(QInvoiceServiceType.invoiceServiceType).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getServiceSales(invoiceServiceTypeQueryResults.getResults()), invoiceServiceTypeQueryResults.getLimit(), invoiceServiceTypeQueryResults.getOffset(), invoiceServiceTypeQueryResults.getTotal(), invoiceServiceTypeQueryResults.isEmpty(), null, prices.stream().mapToDouble(Double::doubleValue).sum());
    }

//    @GetMapping("/get-service-types")
//    @Transactional
//    public List<String> getServices(){
//        List<ServiceType> serviceTypes = appRepository.startJPAQuery(QServiceType.serviceType)
//                .where(QServiceType.serviceType.name.notEqualsIgnoreCase("PLATE NUMBER REGISTRATION"))
//                .fetch();
//        List<String> services = new ArrayList<>();
//        for (ServiceType type: serviceTypes){
//            services.add(type.getName());
//        }
//        return services;
//    }
}
