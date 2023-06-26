package com.jona.msvc.sum.utils;

import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateInterceptor.class);

    @Autowired
    private HttpMsgRepository httpMsgRepository;

    @Override
    @Modifying
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
            HttpMsg httpMsg = new HttpMsg();
            httpMsg.setMethod(request.getMethod().toString());
            httpMsg.setUrl(request.getURI().toString());
            httpMsg.setRequestHeaders(request.getHeaders().toString());
            httpMsg.setRequestBody(new String(body, StandardCharsets.UTF_8).lines().collect(Collectors.joining(System.lineSeparator())));

            if (response != null) {
                httpMsg.setStatus(response.getStatusCode().value());
                httpMsg.setResponseHeaders(response.getHeaders().toString());
                if (response.getBody() != null ) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    org.apache.commons.io.IOUtils.copy(response.getBody(), outputStream);
                    httpMsg.setResponseBody(new String(outputStream.toByteArray(), StandardCharsets.UTF_8).lines().collect(Collectors.joining(System.lineSeparator())));
                }
            } else {
                httpMsg.setStatus(0);
            }

            httpMsgRepository.save(httpMsg);
        } catch (Exception e) {
            logger.error("There was an Exception while trying to intercept httpMsg: " + e.getMessage());
        }
        return response;
    }
}
