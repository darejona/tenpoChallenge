package com.jona.msvc.sum.services;

import com.jona.msvc.sum.configuration.ParametersConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This class is intended to place all the @retryable methods since
 * there's a problem in the library that makes methods calls in the same
 * class to not be retried, they must be placed in other spring component class
 * */
@Service
public class RetryableAPICallService {

    private static final Logger logger = LoggerFactory.getLogger(RetryableAPICallService.class);

    private final RestTemplate restTemplate;
    private final ParametersConfiguration parametersConfiguration;

    public RetryableAPICallService(RestTemplate restTemplate, ParametersConfiguration parametersConfiguration) {
        this.restTemplate = restTemplate;
        this.parametersConfiguration = parametersConfiguration;
    }

    @Retryable(retryFor = Exception.class, maxAttemptsExpression = "${percentage.api.max.attempts}")
    public void callPercentageApi() throws Exception {
        logger.info("attempting to call percentage API");
        restTemplate.getForEntity(parametersConfiguration.percentageMsvcUrl + "/percentage", String.class);
    }

    @Recover
    public void recover(Exception e) {
        logger.error("Call to percentage API failed. Message: " + e.getMessage());
    }
}
