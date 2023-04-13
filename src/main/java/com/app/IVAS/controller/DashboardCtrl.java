package com.app.IVAS.controller;

import com.app.IVAS.dto.DashboardDto;
import com.app.IVAS.dto.PlateNumberRequestDto;
import com.app.IVAS.service.DashboardCtrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/api")

public class DashboardCtrl {
    private final DashboardCtrlService dashboardCtrlService;


    @GetMapping("/generate/dashboard/request")
    @Transactional
    public ResponseEntity<DashboardDto> DashboardReport(){
        DashboardDto dto = dashboardCtrlService.DashboardReport();
        return ResponseEntity.ok(dto);
    }
}
