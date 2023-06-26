package com.jona.msvc.sum.services;

import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class PercentageService {

    private static final Logger logger = LoggerFactory.getLogger(PercentageService.class);

    private final RetryableAPICallService retryableAPICallService;
    private final HttpMsgRepository httpMsgRepository;

    @Scheduled(fixedRateString = "${percentage.api.polling.frequency}")
    public void triggerCallPercentageApi() {
        try {
            retryableAPICallService.callPercentageApi();
        } catch (Exception e) {
            logger.error("There was an exception while retrieving percentage from API:" + e.getMessage());
        }
    }

    public Double calculate(Double value1, Double value2) {
        if (value1 == null || value2 == null) {
            throw new IllegalArgumentException("Both values must be not null");
        }
        Double lastPercentage = getLastQueriedPercentage();
        if (lastPercentage != null) {
            double sum = value1 + value2;
            return sum + (sum * lastPercentage / 100.0);
        } else {
            throw new SchedulingException("There is not any percentage available yet");
        }
    }

    private Double getLastQueriedPercentage() {
        Optional<HttpMsg> httpMsgOptional = httpMsgRepository.findLastSuccessfulPercentageAPICall();
        return httpMsgOptional.map(httpMsg -> Double.valueOf(httpMsg.getResponseBody())).orElse(null);
    }

    public Map<String, Object> getCallHistory(int page, int size, String[] sort) {
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sort));
        Page<HttpMsg> pageHttpMsg = httpMsgRepository.findAll(pagingSort);

        Map<String, Object> response = new HashMap<>();
        response.put("httpMessages", pageHttpMsg.getContent());
        response.put("currentPage", pageHttpMsg.getNumber());
        response.put("totalItems", pageHttpMsg.getTotalElements());
        response.put("totalPages", pageHttpMsg.getTotalPages());
        return response;
    }
}
