package com.app.IVAS.controller;

import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")
public class vehicleCtrl {

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

}
