package com.jona.msvc.sum.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParametersConfiguration {

    @Value("${number.requests.per.minute}")
    public int numberRequestsPerMinute;

    @Value("${msvc.percentage.url}")
    public String percentageMsvcUrl;
}
