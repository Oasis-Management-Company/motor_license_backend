package com.app.IVAS.controller;

import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.*;
import com.app.IVAS.dto.filters.PlateNumberRequestSearchFilter;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QOffense;
import com.app.IVAS.entity.QPlateNumberRequest;
import com.app.IVAS.entity.QServiceType;
import com.app.IVAS.entity.QWorkFlowStage;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.entity.userManagement.QPortalUser;
import com.app.IVAS.repository.OffenseRepository;
import com.app.IVAS.repository.PrefixRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.security.JwtService;
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
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/request")
public class RequestController {

    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final RequestService requestService;
    private final PrefixRepository prefixRepository;
    private final JwtService jwtService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/search/plate-number-request")
    @Transactional
    public QueryResults<PlateNumberRequestPojo> searchPlateNumberRequest(PlateNumberRequestSearchFilter filter){

        JPAQuery<PlateNumberRequest> plateNumberRequestJPAQuery = appRepository.startJPAQuery(QPlateNumberRequest.plateNumberRequest)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (jwtService.user.getRole().getName().equalsIgnoreCase("MLA")){
            plateNumberRequestJPAQuery.where(QPlateNumberRequest.plateNumberRequest.createdBy.id.eq(jwtService.user.getId()));
        }

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
        return ResponseEntity.ok("");
    }

    @PostMapping("/create-work-flow")
    @Transactional
    public ResponseEntity<?> createWorkFlowStages(@RequestBody WorkFlowStageDto dto){
        requestService.CreateWorkFlowStage(dto);
        return ResponseEntity.ok("");
    }

    @PostMapping("/create-service")
    @Transactional
    public ResponseEntity<?> createServiceType(@RequestBody ServiceTypeDto dto){
       requestService.CreateServiceType(dto);
       return ResponseEntity.ok("");
    }

    @PostMapping("/edit-service")
    @Transactional
    public ResponseEntity<?> editServiceType(@RequestBody ServiceTypeDto dto){
        requestService.editServiceType(dto);
        return ResponseEntity.ok("");
    }

    @PostMapping("/update/plate-number-request")
    @Transactional
    public ResponseEntity<?> UpdatePlateNumberRequest(@RequestParam Long requestId,
                                                      @RequestParam String action) throws URISyntaxException {
        requestService.UpdatePlateNumberRequest(requestId, action);
        return ResponseEntity.ok("");
    }

    @GetMapping("/search/service-type")
    @Transactional
    public QueryResults<ServiceTypePojo> searchServiceType(PlateNumberRequestSearchFilter filter){

        JPAQuery<ServiceType> serviceTypeJPAQuery = appRepository.startJPAQuery(QServiceType.serviceType)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            serviceTypeJPAQuery.where(QServiceType.serviceType.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            serviceTypeJPAQuery.where(QServiceType.serviceType.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QServiceType.serviceType);
        QueryResults<ServiceType> serviceTypeQueryResults = serviceTypeJPAQuery.select(QServiceType.serviceType).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(requestService.getServiceTYpe(serviceTypeQueryResults.getResults()), serviceTypeQueryResults.getLimit(), serviceTypeQueryResults.getOffset(), serviceTypeQueryResults.getTotal());
    }

    @GetMapping("/search/work-flow-stage")
    @Transactional
    public QueryResults<WorkFLowStagePojo> searchWorkFlowStage(PlateNumberRequestSearchFilter filter){

        JPAQuery<WorkFlowStage> workFlowStageJPAQuery = appRepository.startJPAQuery(QWorkFlowStage.workFlowStage)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        if (filter.getCreatedAfter() != null){
            workFlowStageJPAQuery.where(QWorkFlowStage.workFlowStage.createdAt.goe(LocalDate.parse(filter.getCreatedAfter(), formatter).atStartOfDay()));
        }

        if (filter.getCreatedBefore() != null){
            workFlowStageJPAQuery.where(QWorkFlowStage.workFlowStage.createdAt.loe(LocalDate.parse(filter.getCreatedBefore(), formatter).atTime(LocalTime.MAX)));
        }

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.DESC), filter.getOrderColumn().orElse("createdAt"), QWorkFlowStage.workFlowStage);
        QueryResults<WorkFlowStage> workFlowStageQueryResults = workFlowStageJPAQuery.select(QWorkFlowStage.workFlowStage).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(requestService.getWorkFlowStage(workFlowStageQueryResults.getResults()), workFlowStageQueryResults.getLimit(), workFlowStageQueryResults.getOffset(), workFlowStageQueryResults.getTotal());
    }

    @PostMapping("/check-approver")
    @Transactional
    public Boolean checkApprover(){
        return requestService.canApproveRequest();
    }

    @GetMapping("/prefix")
    public List<Prefix> getStartCodes(){
        return prefixRepository.findAll().stream().sorted(Comparator.comparing(Prefix::getCode)).collect(Collectors.toList());
    }

    @GetMapping("/get-mla")
    public List<String> getMla(){
        return appRepository.startJPAQuery(QPortalUser.portalUser)
                .select(QPortalUser.portalUser.displayName)
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("MLA"))
                .fetch();
    }

    @GetMapping("/get-vio")
    public List<String> getVio(){
        return appRepository.startJPAQuery(QPortalUser.portalUser)
                .select(QPortalUser.portalUser.displayName)
                .where(QPortalUser.portalUser.role.name.equalsIgnoreCase("VIO"))
                .fetch();
    }

    @GetMapping("/get-offenses")
    public List<String> getOffense(){
        return appRepository.startJPAQuery(QOffense.offense)
                .select(QOffense.offense.name)
                .fetch();
    }

    @GetMapping("/serviceType/view")
    public ResponseEntity<?> viewServiceType(@RequestParam Long rowId){

        return ResponseEntity.ok(requestService.viewServiceType(rowId));
    }

}
