package com.jona.msvc.sum.services;

import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PercentageServiceTest {

    @InjectMocks
    private PercentageService percentageService;

    @Mock
    private RetryableAPICallService retryableAPICallService;

    @Mock
    private HttpMsgRepository httpMsgRepository;

    @Test
    public void triggerCallPercentageApi_Test() throws Exception {
        Mockito.doNothing().when(retryableAPICallService).callPercentageApi();
        percentageService.triggerCallPercentageApi();
    }

    @Test
    public void triggerCallPercentageApi_Test_ExceptionHandling() throws Exception {
        Mockito.doThrow(new RestClientException("")).when(retryableAPICallService).callPercentageApi();
        percentageService.triggerCallPercentageApi();
    }

    @Test
    public void calculate_Test(){
        HttpMsg httpMsg = new HttpMsg();
        httpMsg.setResponseBody("10");
        httpMsg.setStatus(200);
        Mockito.when(httpMsgRepository.findLastSuccessfulPercentageAPICall()).thenReturn(Optional.of(httpMsg));
        Assertions.assertEquals(11.0, percentageService.calculate(5.0,5.0));
    }

    @Test
    public void calculate_Test_Failures(){
        Mockito.when(httpMsgRepository.findLastSuccessfulPercentageAPICall()).thenReturn(Optional.empty());
        Assertions.assertThrows(SchedulingException.class, () -> percentageService.calculate(5.0,5.0), "There is not any percentage available yet");

        Assertions.assertThrows(IllegalArgumentException.class, () -> percentageService.calculate(null,5.0), "Both values must be not null");
    }

    @Test
    public void getCallHistory_Test(){
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
        Mockito.when(httpMsgRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(pageHttpMsg);
        Map<String,Object> result = percentageService.getCallHistory(1,10,new String[]{"id"});
        List<HttpMsg> httpMessagesResult = (List<HttpMsg>) result.get("httpMessages");
        Assertions.assertEquals("10",httpMessagesResult.get(0).getResponseBody());
        Assertions.assertEquals(0,result.get("currentPage"));
        Assertions.assertEquals(1L,result.get("totalItems"));
        Assertions.assertEquals(1,result.get("totalPages"));
    }

}
