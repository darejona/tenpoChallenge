package com.jona.msvc.sum.configuration;

import com.jona.msvc.sum.utils.RestTemplateInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, RestTemplateInterceptor restTemplateInterceptor) {
        return builder.additionalInterceptors(restTemplateInterceptor).build();
    }
}
