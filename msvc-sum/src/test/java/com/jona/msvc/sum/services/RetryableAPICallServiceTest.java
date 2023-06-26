package com.jona.msvc.sum.services;

import com.jona.msvc.sum.configuration.ParametersConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = {RetryableAPICallService.class})
public class RetryableAPICallServiceTest {

    @Autowired
    private RetryableAPICallService retryableAPICallService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ParametersConfiguration parametersConfiguration;

    @Test
    public void callPercentageApi_Test() throws Exception {
        retryableAPICallService.callPercentageApi();
        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(parametersConfiguration.percentageMsvcUrl + "/percentage", String.class);
    }
}
