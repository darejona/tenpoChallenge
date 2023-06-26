package com.jona.msvc.sum.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomBufferingClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private byte[] body;

    public CustomBufferingClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        if (response.getBody() != null) {
            this.body = StreamUtils.copyToByteArray(response.getBody());
        }
    }

    @Override
    public InputStream getBody() throws IOException {
        if (body == null) {
            return null;
        } else {
            return new ByteArrayInputStream(body);
        }
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return response.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }
}

