package com.app.IVAS.controller;

import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.filter.InvoiceSearchFilter;
import com.app.IVAS.filter.SalesSearchFilter;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.VehicleService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")
public class vehicleCtrl {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;

    @GetMapping("/home")
    public String all(){
        return "hellooo";
    }

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/get/vehicle-details")
    public ResponseEntity<InvoiceDto> getUserVehicleDetails(@RequestParam Long id){
        InvoiceDto dto = vehicleService.getUserVehicleDetails(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get/all/vehicle-details")
    public ResponseEntity<VehicleDto> getVehicleDetails(@RequestParam String chasis){
        VehicleDto dto = vehicleService.getVehicleDetails(chasis);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/get/all/vehicle-details/plate-number")
    public ResponseEntity<VehicleDto> getVehicleDetailsByPlate(@RequestParam String plate){
        VehicleDto dto = vehicleService.getVehicleDetailsByPlate(plate);
        return ResponseEntity.ok(dto);
    }
    @PostMapping("/get/all/service-type/plate-number")
    public ResponseEntity<List<ServiceType>> getServiceTypeByPlate(@RequestParam String plate){
        List<ServiceType> dto = vehicleService.getServiceTypeByPlate(plate);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/save/all/service-type/plate-number")
    public ResponseEntity<?> saveServiceTypeByPlate(@RequestParam String myplate, @RequestParam List<Long> ids){
        Invoice dto = vehicleService.saveServiceTypeByPlate(myplate, ids);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/get/type/invoice/edit")
    public ResponseEntity<InvoiceDto> getTypeByInvoiceIdEdit(@RequestParam Long invoiceId){
        return ResponseEntity.ok(vehicleService.getTypeByInvoiceIdEdit(invoiceId));
    }

    @PostMapping("/get/type/invoice/taxpayer")
    public ResponseEntity<?> getTypeByInvoiceTaxpayer(){
        return ResponseEntity.ok(vehicleService.getTypeByInvoiceTaxpayer());
    }

    @PostMapping("/save/all/service-type/taxpayer")
    public ResponseEntity<?> saveServiceTypeByPlateForTaxpayer(@RequestParam String phonenumber, @RequestParam List<Long> ids){
        Invoice dto = vehicleService.saveServiceTypeByPlateForTaxpayer(phonenumber, ids);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/get/all/taxpayer/details")
    public ResponseEntity<?> getTaxpayerByDetails(@RequestParam String phonenumber){
        AsinDto dto = vehicleService.getTaxpayerByDetails(phonenumber);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get/all/taxpayer/details/services")
    public ResponseEntity<List<ServiceType>> getTaxpayerByDetailsServices(){
        List<ServiceType> dto = vehicleService.getTaxpayerByDetailsServices();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/save/all/service-type/taxpayer/id")
    public ResponseEntity<?> saveTaxPayerServiceType(@RequestParam Long id, @RequestParam List<Long> ids){
        Invoice dto = vehicleService.saveTaxPayerServiceType(id, ids);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get/sales/assessments")
    public QueryResults<SalesDto> searchTaxpayerAssessments(SalesSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.plateType.eq(RegType.NON_VEHICLE))
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
        return new QueryResults<>(vehicleService.searchTaxpayerAssessments(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

    @GetMapping("/get/all/invoice")
    public QueryResults<InvoiceDto> searchAllInvoice(InvoiceSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Invoice> userJPAQuery = appRepository.startJPAQuery(QInvoice.invoice)
                .where(predicateExtractor.getPredicate(filter))
                .where(QInvoice.invoice.paymentStatus.eq(PaymentStatus.PAID))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        if (filter.getAfter()!= null && !filter.getAfter().equals("")) {
            LocalDate startDate =  LocalDate.parse(filter.getAfter(), formatter);
            userJPAQuery.where(QInvoice.invoice.createdAt.goe(startDate.atStartOfDay()));
        }
        if (filter.getBefore() != null && !filter.getBefore().equals("")) {
            LocalDate endDate = LocalDate.parse(filter.getBefore(), formatter);
            userJPAQuery.where(QInvoice.invoice.createdAt.loe(endDate.atTime(LocalTime.MAX)));

        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QInvoice.invoice);
        QueryResults<Invoice> userQueryResults = userJPAQuery.select(QInvoice.invoice).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleService.searchAllInvoice(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }
}
