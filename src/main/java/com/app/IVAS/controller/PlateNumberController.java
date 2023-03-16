package com.app.IVAS.controller;


import com.app.IVAS.dto.PlateNumberDto;
import com.app.IVAS.service.PlateNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plate-number")
public class PlateNumberController {

    private final PlateNumberService plateNumberService;


//    @PostMapping("/generate")
//    public ResponseEntity<?> generatePlateNumber(@RequestBody PlateNumberDto dto){
//       String response
//    }

}
