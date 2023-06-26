package com.jona.msvc.percentage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Service
@EnableScheduling
public class PercentageService {

    private static final Logger logger = LoggerFactory.getLogger(PercentageService.class);

    private Double currentPercentage;

    /**
     * This method generates a new random 2 decimal digits percentage based
     * on the frequency set up on the property percentage.random.generation.frequency
     * */
    @Scheduled(fixedRateString = "${percentage.random.generation.frequency}")
    public void setRandomPercentage() {
        currentPercentage = Math.round(new Random().nextDouble(20) * 100.0) / 100.0 ;
        logger.info("New random percentage generated: " + currentPercentage);
    }

    public Double getCurrentPercentage() {
        return Objects.requireNonNullElse(currentPercentage, 0.0);
    }
}
