package com.app.IVAS.controller;

import com.app.IVAS.Utils.PredicateExtractor;
import com.app.IVAS.dto.VehicleMakeAndModelDto;
import com.app.IVAS.dto.filters.VehicleMakeSearchFilter;
import com.app.IVAS.dto.filters.VehicleModelSearchFilter;
import com.app.IVAS.entity.*;
import com.app.IVAS.entity.QVehicleMake;
import com.app.IVAS.entity.QVehicleModel;
import com.app.IVAS.repository.VehicleModelRepository;
import com.app.IVAS.repository.app.AppRepository;
import com.app.IVAS.service.VehicleMakeAndModelService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@Slf4j
@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-make_model")
public class VehicleMakeAndModelController {

    private final VehicleMakeAndModelService vehicleMakeAndModelService;
    private final AppRepository appRepository;
    private final PredicateExtractor predicateExtractor;
    private final VehicleModelRepository vehicleModelRepository;

    @PostMapping("/create-edit")
    public ResponseEntity<?> createAndEdit(@RequestBody VehicleMakeAndModelDto dto) {

        return ResponseEntity.ok(vehicleMakeAndModelService.createVehicleMake(dto));
    }

    @GetMapping("/all-models")
    public QueryResults<VehicleModel> searchVehicleModel(VehicleModelSearchFilter filter) {

        JPAQuery<VehicleModel> vehicleModelJPAQuery = appRepository.startJPAQuery(QVehicleModel.vehicleModel)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));

        vehicleModelJPAQuery.where(QVehicleModel.vehicleModel.vehicleMake.name.equalsIgnoreCase(filter.getName()));

        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.ASC), filter.getOrderColumn().orElse("name"), QVehicleModel.vehicleModel);
        QueryResults<VehicleModel> vehicleModelQueryResults = vehicleModelJPAQuery.select(QVehicleModel.vehicleModel).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleMakeAndModelService.getVehicleModels(vehicleModelQueryResults.getResults()), vehicleModelQueryResults.getLimit(), vehicleModelQueryResults.getOffset(), vehicleModelQueryResults.getTotal());
    }





    @GetMapping("/search-vehicle-make")
    public QueryResults<VehicleMake> searchVehicleMake(VehicleMakeSearchFilter filter) {

        JPAQuery<VehicleMake> vehicleMakeJPAQuery = appRepository.startJPAQuery(QVehicleMake.vehicleMake)
                .where(predicateExtractor.getPredicate(filter))
                .offset(filter.getOffset().orElse(0))
                .limit(filter.getLimit().orElse(10));


        OrderSpecifier<?> sortedColumn = appRepository.getSortedColumn(filter.getOrder().orElse(Order.ASC), filter.getOrderColumn().orElse("name"), QVehicleMake.vehicleMake);
        QueryResults<VehicleMake> vehicleMakeQueryResults = vehicleMakeJPAQuery.select(QVehicleMake.vehicleMake).distinct().orderBy(sortedColumn).fetchResults();
        return new QueryResults<>(vehicleMakeAndModelService.get(vehicleMakeQueryResults.getResults()), vehicleMakeQueryResults.getLimit(), vehicleMakeQueryResults.getOffset(), vehicleMakeQueryResults.getTotal());
    }


}
