package com.app.IVAS.controller;

import com.app.IVAS.dto.DashboardDto;
import com.app.IVAS.dto.VioDashboardDto;
import com.app.IVAS.service.VioDashboardService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", maxAge = 5600)
@RequiredArgsConstructor
@RestController
@RequestMapping(name = "/vio")

public class VioDashboard {

    private final VioDashboardService vioDashboardService;

    @GetMapping("/vio/dashboard")
    public ResponseEntity<VioDashboardDto> VioDashboardReport(){
        VioDashboardDto dto = vioDashboardService.VioDashboardReport();
        return ResponseEntity.ok(dto);
    }

}
