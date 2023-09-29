package com.linh.freshfoodbackend.controller;

import com.linh.freshfoodbackend.service.IChartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/chart")
@AllArgsConstructor
@Slf4j
public class ChartController {

    private final IChartService chartService;

    @GetMapping(path = "/card")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cardChart(){
        return ResponseEntity.ok(chartService.cardChart());
    }

    @GetMapping(path = "/area")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> areaChart(){
        return ResponseEntity.ok(chartService.areaChart());
    }

    @GetMapping(path = "/pie")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> pieChart(){
        return ResponseEntity.ok(chartService.pieChart());
    }
}
