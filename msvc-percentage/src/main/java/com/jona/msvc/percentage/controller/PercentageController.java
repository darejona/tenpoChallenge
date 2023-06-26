package com.jona.msvc.percentage.controller;

import com.jona.msvc.percentage.service.PercentageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PercentageController {

    @Autowired
    PercentageService percentageService;

    @GetMapping("/percentage")
    public ResponseEntity<Double> getPercentage() {
        return ResponseEntity.ok(percentageService.getCurrentPercentage());
    }
}
