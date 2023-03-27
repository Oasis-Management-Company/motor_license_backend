package com.app.IVAS.controller;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QVehicle;
import com.app.IVAS.filter.SalesSearchFilter;
import com.app.IVAS.filter.VehicleSerachFilter;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.SalesCtrlService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")
public class SalesCtrl {

    @Autowired
    private SalesCtrlService service;

    private final AppRepository appRepository;

    private final PredicateExtractor predicateExtractor;

    @PostMapping("/save/sales")
    public ResponseEntity<SalesDto> SaveSales(@RequestBody SalesDto sales){
        return ResponseEntity.ok(service.SaveSales(sales));
    }

    @PostMapping("/get/sales")
    public QueryResults<SalesDto> searchDirectTax(SalesSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
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
        return new QueryResults<>(service.GetSales(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

    @GetMapping("/validate-asin")
    public ResponseEntity<AsinDto> ValidateAsin(@RequestParam String asin){
        AsinDto dto = service.ValidateAsin(asin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/vehicle-make")
    public ResponseEntity<List<VehicleMake>> getVehicleMake(){
        List<VehicleMake> dto = service.getVehicleMake();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/vehicle-model")
    public ResponseEntity<List<VehicleModel>> getVehicleModel(@RequestParam long id){
        List<VehicleModel> dto = service.getVehicleModel(id);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/vehicle-category")
    public ResponseEntity<List<VehicleCategory>> getVehicleCategory(){
        List<VehicleCategory> dto = service.getVehicleCategory();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user-plate")
    public ResponseEntity<List<PlateNumber>> getUserPlateNumbers(@RequestParam Long id){
        List<PlateNumber> dto = service.getUserPlateNumbers(id);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/plate-number/type")
    public ResponseEntity<List<PlateNumberType>> getUserPlateNumberTypes(){
        List<PlateNumberType> dto = service.getUserPlateNumberTypes();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/service-type/id")
    public ResponseEntity<List<ServiceType>> getServiceType(@RequestParam Long id){
        List<ServiceType> dto = service.getServiceType(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/sales/approval/id")
    public ResponseEntity<?> getApprovalStatus(@RequestParam("id") Long id,
                                               @RequestParam("action") String action,
                                               @RequestParam("reason") Optional<String> reason)
    {
        String response = service.getApprovalStatus(id, action, reason);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get/sales/vio")
    public QueryResults<SalesDto> searchAllForVIO(SalesSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
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
        return new QueryResults<>(service.searchAllForVIO(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

    @PostMapping("/get/vehicles")
    public QueryResults<VehicleDto> searchAllVehicles(VehicleSerachFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Vehicle> userJPAQuery = appRepository.startJPAQuery(QVehicle.vehicle)
                .where(predicateExtractor.getPredicate(filter))
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

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QVehicle.vehicle);
        QueryResults<Vehicle> userQueryResults = userJPAQuery.select(QVehicle.vehicle).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(service.searchAllVehicles(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

    @PostMapping("/add/vehicle")
    public ResponseEntity<SalesDto> AddVehicle(@RequestBody SalesDto sales){
        return ResponseEntity.ok(service.AddVehicle(sales));
    }

    @PostMapping("/get/serviceType")
    public ResponseEntity<List<ServiceType>> getServiceTypeByCategory(@RequestParam Long categoryId){
        return ResponseEntity.ok(service.getServiceTypeByCategory(categoryId));
    }

}