package com.app.IVAS.controller;

import com.app.IVAS.dto.VehicleMakeAndModelDto;
import com.app.IVAS.entity.PlateNumber;
import com.app.IVAS.entity.QPlateNumber;
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
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-make_model")
public class VehicleMakeAndModelController {

private final VehicleMakeAndModelService vehicleMakeAndModelService;

    @PostMapping("/create-edit")
    public ResponseEntity<?> createAndEdit(@RequestBody VehicleMakeAndModelDto dto){ ;
        return ResponseEntity.ok(vehicleMakeAndModelService.createVehicleMake(dto));
    }



}
