package com.app.IVAS.controller;


import com.app.IVAS.Enum.PlateNumberStatus;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.*;
import com.app.IVAS.dto.filters.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoiceOffenseType;
import com.app.IVAS.entity.QInvoiceServiceType;
import com.app.IVAS.entity.QPlateNumber;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QVehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.entity.userManagement.QZonalOffice;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.ReportService;
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

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER VEHICLE")
                        .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER MOTORCYCLE")))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<InvoiceServiceType> typeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER VEHICLE")
                        .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER MOTORCYCLE")))
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

        if (filter.getPlateNumber() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.plateNumber.eq(filter.getPlateNumber()));

            typeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.plateNumber.eq(filter.getPlateNumber()));
        }

        if (filter.getType() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.type.id.eq(filter.getType()));

            typeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.type.id.eq(filter.getType()));
        }

        if (filter.getZone() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .where(QPortalUser.portalUser.office.id.eq(filter.getZone()));

            typeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .where(QPortalUser.portalUser.office.id.eq(filter.getZone()));
        }

        List<Double> prices = new ArrayList<>();
        for(InvoiceServiceType serviceType:typeJPAQuery.fetch()){
            prices.add(serviceType.getAmount());
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceServiceType.invoiceServiceType);
        QueryResults<InvoiceServiceType> invoiceServiceTypeQueryResults = invoiceServiceTypeJPAQuery.select(QInvoiceServiceType.invoiceServiceType).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getSales(invoiceServiceTypeQueryResults.getResults()), invoiceServiceTypeQueryResults.getLimit(), invoiceServiceTypeQueryResults.getOffset(), invoiceServiceTypeQueryResults.getTotal(), invoiceServiceTypeQueryResults.isEmpty(), null, prices.size() > 0 ? prices.stream().mapToDouble(Double::doubleValue).sum() : 0.00);
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
                .where(QPlateNumber.plateNumber1.stock.isNotNull())
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
    public QueryResultsPojo<SalesReportPojo> searchServiceSales(ServiceReportSearchFilter filter){

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER VEHICLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER MOTORCYCLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<InvoiceServiceType> typeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER VEHICLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER MOTORCYCLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull());

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
        }

        if(jwtService.user.getRole().getName().equalsIgnoreCase("VIO")){
            getVIOService(invoiceServiceTypeJPAQuery);
            getVIOService(typeJPAQuery);
        }

        if (filter.getCreatedAfter() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
            typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        if (filter.getZone() != null){
            invoiceServiceTypeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .leftJoin(QZonalOffice.zonalOffice).on(QZonalOffice.zonalOffice.eq(QPortalUser.portalUser.office))
                    .where(QZonalOffice.zonalOffice.id.eq(filter.getZone()));

            typeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .leftJoin(QZonalOffice.zonalOffice).on(QZonalOffice.zonalOffice.eq(QPortalUser.portalUser.office))
                    .where(QZonalOffice.zonalOffice.id.eq(filter.getZone()));
        }

        List<Double> prices = new ArrayList<>();
        for(InvoiceServiceType serviceType:typeJPAQuery.fetch()){
            prices.add(serviceType.getAmount());
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceServiceType.invoiceServiceType);
        QueryResults<InvoiceServiceType> invoiceServiceTypeQueryResults = invoiceServiceTypeJPAQuery.select(QInvoiceServiceType.invoiceServiceType).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getServiceSales(invoiceServiceTypeQueryResults.getResults()), invoiceServiceTypeQueryResults.getLimit(), invoiceServiceTypeQueryResults.getOffset(), invoiceServiceTypeQueryResults.getTotal(), invoiceServiceTypeQueryResults.isEmpty(), null, prices.size() > 0 ? prices.stream().mapToDouble(Double::doubleValue).sum() : 0.00);
    }

    private void getVIOService(JPAQuery<InvoiceServiceType> typeJPAQuery) {
        typeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("LEARNER PERMIT")
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.equalsIgnoreCase("VEHICLE REG -VEHICLE TEST/ROAD WORTHINESS (PRIVATE)"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("ROADWORTHINESS/COMPUTERIZED VEHICLE"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("VEHICLE TEST/ROADWORTHINESS"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("Test-MotorCycle/KEKE"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("TRICYCLE DRIVING TEST FEE"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("RENEWAL OF DRIVING TEST FEE"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("RENEWAL OF TRICYCLE DRIVING TEST FEE"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("DRIVERS TEST"))
                .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("DRIVING TEST FEE")));
    }

    @GetMapping("/search/vio-report")
    @Transactional
    public QueryResults<VIOReportPojo> searchVIOReport(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("VIO"))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(reportService.getVIOReport(portalUserQueryResults.getResults(), filter.getCreatedBefore(), filter.getCreatedAfter()), portalUserQueryResults.getLimit(), portalUserQueryResults.getOffset(), portalUserQueryResults.getTotal());

    }

    @GetMapping("/search/offense-report")
    @Transactional
    public QueryResultsPojo<OffenseReportPojo> searchOffenseReport(OffenseReportSearchFilter filter){

        JPAQuery<InvoiceOffenseType> invoiceOffenseTypeJPAQuery = appRepository.startJPAQuery(QInvoiceOffenseType.invoiceOffenseType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.isNotNull())
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        JPAQuery<InvoiceOffenseType> typeJPAQuery = appRepository.startJPAQuery(QInvoiceOffenseType.invoiceOffenseType)
                .where(predicateExtractor.getPredicate(filter));

        if (filter.getCreatedAfter() != null){
            invoiceOffenseTypeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
            typeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceOffenseTypeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
            typeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        if (filter.getPlateNumber() != null){
            invoiceOffenseTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceOffenseType.invoiceOffenseType.invoice.vehicle))
                    .leftJoin(QPlateNumber.plateNumber1).on(QPlateNumber.plateNumber1.eq(QVehicle.vehicle.plateNumber))
                    .where(QPlateNumber.plateNumber1.plateNumber.containsIgnoreCase(filter.getPlateNumber()));

            typeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceOffenseType.invoiceOffenseType.invoice.vehicle))
                    .leftJoin(QPlateNumber.plateNumber1).on(QPlateNumber.plateNumber1.eq(QVehicle.vehicle.plateNumber))
                    .where(QPlateNumber.plateNumber1.plateNumber.containsIgnoreCase(filter.getPlateNumber()));
        }

        List<Double> prices = new ArrayList<>();
        for(InvoiceOffenseType offenseType:typeJPAQuery.fetch()){
            prices.add(offenseType.getAmount());
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceOffenseType.invoiceOffenseType);
        QueryResults<InvoiceOffenseType> invoiceOffenseTypeQueryResults = invoiceOffenseTypeJPAQuery.select(QInvoiceOffenseType.invoiceOffenseType).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResultsPojo<>(reportService.getOffenseReport(invoiceOffenseTypeQueryResults.getResults()), invoiceOffenseTypeQueryResults.getLimit(), invoiceOffenseTypeQueryResults.getOffset(), invoiceOffenseTypeQueryResults.getTotal(), invoiceOffenseTypeQueryResults.isEmpty(), null, prices.stream().mapToDouble(Double::doubleValue).sum());

    }

    @PostMapping(path = "/download-stock-report")
    @Transactional
    public ResponseEntity<Resource> exportStockReport(PortalUserSearchFilter filter, HttpServletRequest request) throws Exception {
        List<StockReportPojo> pojos = getStockReportPojo(filter);
        Resource resource = reportService.exportStockReport(pojos, filter.getDownloadType());
        String contentType = null;
        String fileName = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null){
            if (filter.getDownloadType().equalsIgnoreCase("pdf")){
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        if (filter.getDownloadType().equalsIgnoreCase("pdf")){
            fileName = "mla_stock_report.pdf";
        } else {
            fileName = "mla_stock_report.xlsx";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

    @PostMapping(path = "/download-plate-number-sales-report")
    @Transactional
    public ResponseEntity<Resource> exportPlateNumberSalesReport(SalesReportSearchFilter filter, HttpServletRequest request) throws Exception {
        List<SalesReportDto> pojos = getSalesReportDto(filter);
        Resource resource = reportService.exportPlateNumberSalesReport(pojos, filter.getDownloadType());
        String contentType = null;
        String fileName = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null){
            if (filter.getDownloadType().equalsIgnoreCase("pdf")){
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        if (filter.getDownloadType().equalsIgnoreCase("pdf")){
            fileName = "plate-number_sales_report.pdf";
        } else {
            fileName = "plate-number_sales_report.xlsx";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

    @PostMapping(path = "/download-service-sales-report")
    @Transactional
    public ResponseEntity<Resource> exportServiceSalesReport(ServiceReportSearchFilter filter, HttpServletRequest request) throws Exception {
        List<SalesReportPojo> pojos = getServiceSalesPojo(filter);
        Resource resource = reportService.exportServiceSalesReport(pojos, filter.getDownloadType());
        String contentType = null;
        String fileName = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null){
            if (filter.getDownloadType().equalsIgnoreCase("pdf")){
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        if (filter.getDownloadType().equalsIgnoreCase("pdf")){
            fileName = "service_sales_report.pdf";
        } else {
            fileName = "service_sales_report.xlsx";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

    @PostMapping(path = "/download-vio-report")
    @Transactional
    public ResponseEntity<Resource> exportVIOReport(PortalUserSearchFilter filter, HttpServletRequest request) throws Exception {
        List<VIOReportPojo> pojos = getVIOReportPojo(filter);
        Resource resource = reportService.exportVIOReport(pojos, filter.getDownloadType());
        String contentType = null;
        String fileName = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null){
            if (filter.getDownloadType().equalsIgnoreCase("pdf")){
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        if (filter.getDownloadType().equalsIgnoreCase("pdf")){
            fileName = "vio_report.pdf";
        } else {
            fileName = "vio_report.xlsx";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

    @PostMapping(path = "/download-offense-report")
    @Transactional
    public ResponseEntity<Resource> exportOffenseReport(OffenseReportSearchFilter filter, HttpServletRequest request) throws Exception {
        List<OffenseReportPojo> pojos = getOffenseReportPojo(filter);
        Resource resource = reportService.exportOffenseReport(pojos, filter.getDownloadType());
        String contentType = null;
        String fileName = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if (contentType == null){
            if (filter.getDownloadType().equalsIgnoreCase("pdf")){
                contentType = "application/pdf";
            } else {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        if (filter.getDownloadType().equalsIgnoreCase("pdf")){
            fileName = "offense_report.pdf";
        } else {
            fileName = "offenses_report.xlsx";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(resource);
    }

// TODO:  <============================================================ private print methods ============================================================>

    private List<StockReportPojo> getStockReportPojo(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("MLA"));

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return reportService.getStockReport(portalUserQueryResults.getResults());
    }

    private List<SalesReportPojo> getServiceSalesPojo(ServiceReportSearchFilter filter){

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER VEHICLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.notLike("PLATE NUMBER MOTORCYCLE%"))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull());


        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
        }

        if(jwtService.user.getRole().getName().equalsIgnoreCase("VIO")){
            getVIOService(invoiceServiceTypeJPAQuery);
        }

        if (filter.getCreatedAfter() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        if (filter.getZone() != null){
            invoiceServiceTypeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .leftJoin(QZonalOffice.zonalOffice).on(QZonalOffice.zonalOffice.eq(QPortalUser.portalUser.office))
                    .where(QZonalOffice.zonalOffice.id.eq(filter.getZone()));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceServiceType.invoiceServiceType);
        QueryResults<InvoiceServiceType> invoiceServiceTypeQueryResults = invoiceServiceTypeJPAQuery.select(QInvoiceServiceType.invoiceServiceType).distinct().orderBy(sortedColumn).fetchResults();
        return reportService.getServiceSales(invoiceServiceTypeQueryResults.getResults());
    }

    private List<SalesReportDto> getSalesReportDto(SalesReportSearchFilter filter){

        JPAQuery<InvoiceServiceType> invoiceServiceTypeJPAQuery = appRepository.startJPAQuery(QInvoiceServiceType.invoiceServiceType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER VEHICLE")
                        .or(QInvoiceServiceType.invoiceServiceType.serviceType.name.startsWithIgnoreCase("PLATE NUMBER MOTORCYCLE")))
                .where(QInvoiceServiceType.invoiceServiceType.PaymentDate.isNotNull());

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.invoice.createdBy.displayName.equalsIgnoreCase(jwtService.user.getDisplayName()));
        }

        if (filter.getCreatedAfter() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceServiceTypeJPAQuery.where(QInvoiceServiceType.invoiceServiceType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        if (filter.getPlateNumber() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.plateNumber.eq(filter.getPlateNumber()));
        }

        if (filter.getType() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceServiceType.invoiceServiceType.invoice.vehicle))
                    .where(QVehicle.vehicle.plateNumber.type.id.eq(filter.getType()));
        }

        if (filter.getZone() !=  null){
            invoiceServiceTypeJPAQuery.leftJoin(QPortalUser.portalUser).on(QPortalUser.portalUser.eq(QInvoiceServiceType.invoiceServiceType.invoice.createdBy))
                    .where(QPortalUser.portalUser.office.id.eq(filter.getZone()));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceServiceType.invoiceServiceType);
        QueryResults<InvoiceServiceType> invoiceServiceTypeQueryResults = invoiceServiceTypeJPAQuery.select(QInvoiceServiceType.invoiceServiceType).distinct().orderBy(sortedColumn).fetchResults();
        return reportService.getSales(invoiceServiceTypeQueryResults.getResults());
    }

    private List<VIOReportPojo> getVIOReportPojo(PortalUserSearchFilter filter){

        JPAQuery<PortalUser> portalUserJPAQuery = appRepository.startJPAQuery(QPortalUser.portalUser)
                .where(predicateExtractor.getPredicate(filter))
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("VIO"))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QPortalUser.portalUser);
        QueryResults<PortalUser> portalUserQueryResults = portalUserJPAQuery.select(QPortalUser.portalUser).distinct().orderBy(sortedColumn).fetchResults();
        return reportService.getVIOReport(portalUserQueryResults.getResults(), filter.getCreatedBefore(), filter.getCreatedAfter());

    }

    private List<OffenseReportPojo> getOffenseReportPojo(OffenseReportSearchFilter filter){

        JPAQuery<InvoiceOffenseType> invoiceOffenseTypeJPAQuery = appRepository.startJPAQuery(QInvoiceOffenseType.invoiceOffenseType)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.isNotNull());

        if (filter.getCreatedAfter() != null){
            invoiceOffenseTypeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            invoiceOffenseTypeJPAQuery.where(QInvoiceOffenseType.invoiceOffenseType.PaymentDate.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        if (filter.getPlateNumber() != null){
            invoiceOffenseTypeJPAQuery.leftJoin(QVehicle.vehicle).on(QVehicle.vehicle.eq(QInvoiceOffenseType.invoiceOffenseType.invoice.vehicle))
                    .leftJoin(QPlateNumber.plateNumber1).on(QPlateNumber.plateNumber1.eq(QVehicle.vehicle.plateNumber))
                    .where(QPlateNumber.plateNumber1.plateNumber.containsIgnoreCase(filter.getPlateNumber()));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("PaymentDate"), QInvoiceOffenseType.invoiceOffenseType);
        QueryResults<InvoiceOffenseType> invoiceOffenseTypeQueryResults = invoiceOffenseTypeJPAQuery.select(QInvoiceOffenseType.invoiceOffenseType).distinct().orderBy(sortedColumn).fetchResults();
        return reportService.getOffenseReport(invoiceOffenseTypeQueryResults.getResults());

    }
}
