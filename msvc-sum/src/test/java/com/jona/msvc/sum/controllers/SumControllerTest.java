package com.jona.msvc.sum.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jona.msvc.sum.configuration.ParametersConfiguration;
import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import com.jona.msvc.sum.services.PercentageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.scheduling.SchedulingException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SumController.class)
public class SumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PercentageService percentageService;

    @MockBean
    private HttpMsgRepository httpMsgRepository;

    @Autowired
    private ParametersConfiguration parametersConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getSum_test() throws Exception {
        double expectedResult = 5.0;
        Mockito.when(percentageService.calculate(2.0, 3.0)).thenReturn(expectedResult);
        mockMvc.perform(MockMvcRequestBuilders.get("/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("value1","2")
                        .param("value2","3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(expectedResult)));
    }

    @Test
    public void getSum_test_ServiceUnavailable_failure() throws Exception {
        Mockito.when(percentageService.calculate(2.0, 3.0)).thenThrow(SchedulingException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("value1","2")
                        .param("value2","3"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void getSum_test_BadRequest_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/sum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("value2","3"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getCallHistory_Test() throws Exception {
        HttpMsg httpMsg = new HttpMsg();
        httpMsg.setResponseBody("10");
        httpMsg.setStatus(200);
        httpMsg.setMethod("GET");
        httpMsg.setRequestHeaders("""
                [Accept:"text/plain, application/json, application/*+json, */*", Content-Length:"0"]
                """);
        httpMsg.setResponseHeaders("""
                [Content-Type:"application/json", Transfer-Encoding:"chunked", Date:"Sat, 24 Jun 2023 13:38:32 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]
                """);
        httpMsg.setUrl("http://localhost:8002/percentage");
        List<HttpMsg> httpMsgs = new ArrayList<>();
        httpMsgs.add(httpMsg);
        PageImpl<HttpMsg> pageHttpMsg = new PageImpl<>(httpMsgs);
        Map<String, Object> response = new HashMap<>();
        response.put("httpMessages", pageHttpMsg.getContent());
        response.put("currentPage", pageHttpMsg.getNumber());
        response.put("totalItems", pageHttpMsg.getTotalElements());
        response.put("totalPages", pageHttpMsg.getTotalPages());
        Mockito.when(percentageService.getCallHistory(0,10, new String[] {"id"})).thenReturn(response);
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/callHistory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(response)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);

        Assertions.assertEquals("10",((LinkedHashMap<String,String>)((ArrayList<Object>)responseMap.get("httpMessages")).get(0)).get("responseBody"));
        Assertions.assertEquals(0,responseMap.get("currentPage"));
        Assertions.assertEquals(1,responseMap.get("totalItems"));
        Assertions.assertEquals(1,responseMap.get("totalPages"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ParametersConfiguration parametersConfiguration() {
            ParametersConfiguration parametersConfiguration = new ParametersConfiguration();
            parametersConfiguration.numberRequestsPerMinute = 10;
            parametersConfiguration.percentageMsvcUrl = "http://localhost:8080";
            return parametersConfiguration;
        }
    }
}

