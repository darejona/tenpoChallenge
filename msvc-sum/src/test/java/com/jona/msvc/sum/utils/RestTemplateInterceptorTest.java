package com.jona.msvc.sum.utils;

import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestTemplateInterceptorTest {

    @Mock
    private HttpMsgRepository httpMsgRepository;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    RestTemplateInterceptor interceptor;

    @Test
    public void intercept_test() throws Exception {
        HttpRequest request = new HttpRequest() {
            @Override
            public HttpHeaders getHeaders() {
                return new HttpHeaders();
            }

            @Override
            public HttpMethod getMethod() {
                return HttpMethod.GET;
            }

            @Override
            public URI getURI() {
                try {
                    return new URI("http://localhost");
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        byte[] body = "test body".getBytes();
        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(response.getHeaders()).thenReturn(HttpHeaders.EMPTY);
        when(response.getBody()).thenReturn(InputStream.nullInputStream());
        when(execution.execute(request, body)).thenReturn(response);

        interceptor.intercept(request, body, execution);

        verify(httpMsgRepository).save(any(HttpMsg.class));
    }
}
