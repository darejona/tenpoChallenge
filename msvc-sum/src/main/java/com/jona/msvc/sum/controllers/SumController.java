package com.jona.msvc.sum.controllers;

import com.jona.msvc.sum.configuration.ParametersConfiguration;
import com.jona.msvc.sum.services.PercentageService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.SchedulingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
public class SumController {

    private final Bucket bucket;

    private final PercentageService percentageService;

    private final int numberRequestsPerMinute;

    public SumController(ParametersConfiguration parametersConfiguration, PercentageService percentageService) {
        if (parametersConfiguration.numberRequestsPerMinute > 0) {
            numberRequestsPerMinute = parametersConfiguration.numberRequestsPerMinute;
        } else {
            numberRequestsPerMinute = 1;
        }
        Bandwidth limit = Bandwidth.classic(parametersConfiguration.numberRequestsPerMinute, Refill.intervally(numberRequestsPerMinute, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
        this.percentageService = percentageService;
    }

    @GetMapping("/sum")
    public ResponseEntity<?> getSum(@RequestParam Double value1, @RequestParam Double value2){
        if (bucket.tryConsume(1)) {
            try {
                double result = percentageService.calculate(value1, value2);
                return ResponseEntity.ok(result);
            } catch (IllegalArgumentException illegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(illegalArgumentException.getMessage());
            } catch (SchedulingException schedulingException) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(schedulingException.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Only %d request/s are allowed per minute".formatted(numberRequestsPerMinute));
    }

    @GetMapping("/callHistory")
    public ResponseEntity<?> getCallHistory(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "id") String[] sort) {

        Map<String, Object> response = percentageService.getCallHistory(page,size,sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
