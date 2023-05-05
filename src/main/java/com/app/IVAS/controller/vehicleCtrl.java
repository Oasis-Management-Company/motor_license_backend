package com.app.IVAS.controller;

import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QInvoice;
import com.app.IVAS.entity.QSales;
import com.app.IVAS.entity.QVehicle;
import com.app.IVAS.dto.filters.PortalUserSearchFilter;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.filter.InvoiceSearchFilter;
import com.app.IVAS.filter.VehicleSerachFilter;
import com.app.IVAS.repository.VehicleCategoryRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.VehicleService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")
public class vehicleCtrl {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final JwtService jwtService;
    private final VehicleCategoryRepository vehicleCategoryRepository;

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
    public QueryResults<PortalUserPojo> searchTaxpayerAssessments(PortalUserSearchFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Sales> userJPAQuery = appRepository.startJPAQuery(QSales.sales)
                .where(predicateExtractor.getPredicate(filter))
                .where(QSales.sales.plateType.eq(RegType.NON_VEHICLE))
                .where(QSales.sales.createdBy.id.eq(jwtService.user.getId()))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

//        userJPAQuery.where(QPortalUser.portalUser.role.id.eq(4L));

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QSales.sales);
        QueryResults<Sales> userQueryResults = userJPAQuery.select(QSales.sales).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleService.searchTaxpayerAssessment(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
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

    @GetMapping("/get/all/vehicle/edit")
    public QueryResults<VehicleDto> searchAllVehicleForApproval(VehicleSerachFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Vehicle> userJPAQuery = appRepository.startJPAQuery(QVehicle.vehicle)
                .where(predicateExtractor.getPredicate(filter))
                .where(QVehicle.vehicle.regType.eq(RegType.EDIT))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        if (filter.getAfter()!= null && !filter.getAfter().equals("")) {
            LocalDate startDate =  LocalDate.parse(filter.getAfter(), formatter);
            userJPAQuery.where(QVehicle.vehicle.createdAt.goe(startDate.atStartOfDay()));
        }
        if (filter.getBefore() != null && !filter.getBefore().equals("")) {
            LocalDate endDate = LocalDate.parse(filter.getBefore(), formatter);
            userJPAQuery.where(QVehicle.vehicle.createdAt.loe(endDate.atTime(LocalTime.MAX)));

        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QVehicle.vehicle);
        QueryResults<Vehicle> userQueryResults = userJPAQuery.select(QVehicle.vehicle).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleService.searchAllVehicleForApproval(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }

    @GetMapping("/get/mla/vehicle/edit")
    public QueryResults<VehicleDto> searchMlaVehicleForApproval(VehicleSerachFilter filter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JPAQuery<Vehicle> userJPAQuery = appRepository.startJPAQuery(QVehicle.vehicle)
                .where(predicateExtractor.getPredicate(filter))
                .where(QVehicle.vehicle.regType.eq(RegType.EDIT))
                .where(QVehicle.vehicle.createdBy.id.eq(jwtService.user.getId()))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        if (filter.getAfter()!= null && !filter.getAfter().equals("")) {
            LocalDate startDate =  LocalDate.parse(filter.getAfter(), formatter);
            userJPAQuery.where(QVehicle.vehicle.createdAt.goe(startDate.atStartOfDay()));
        }
        if (filter.getBefore() != null && !filter.getBefore().equals("")) {
            LocalDate endDate = LocalDate.parse(filter.getBefore(), formatter);
            userJPAQuery.where(QVehicle.vehicle.createdAt.loe(endDate.atTime(LocalTime.MAX)));

        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QVehicle.vehicle);
        QueryResults<Vehicle> userQueryResults = userJPAQuery.select(QVehicle.vehicle).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleService.searchAllVehicleForApproval(userQueryResults.getResults()), userQueryResults.getLimit(), userQueryResults.getOffset(), userQueryResults.getTotal());
    }


    @GetMapping("/get/all/edit/details/services")
    public ResponseEntity<VehicleEditDto> getAllEditVehicle(@RequestParam Long id){
        VehicleEditDto dto = vehicleService.getAllEditVehicle(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/approve/all/edit/details/services")
    public HttpStatus approveEdittedVehicle(@RequestParam Long id, @RequestParam String type){
        return vehicleService.approveEdittedVehicle(id, type);
    }


}
