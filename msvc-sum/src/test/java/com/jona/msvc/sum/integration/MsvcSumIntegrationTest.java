package com.jona.msvc.sum.integration;

import com.jona.msvc.sum.models.entities.HttpMsg;
import com.jona.msvc.sum.repositories.HttpMsgRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true", "number.requests.per.minute=10"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureEmbeddedDatabase( type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES, provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
public class MsvcSumIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    HttpMsgRepository httpMsgRepository;


    @Test
    public void testGetSum()  {
        HttpMsg httpMsg = new HttpMsg();
        httpMsg.setStatus(200);
        httpMsg.setResponseBody("10");
        httpMsgRepository.save(httpMsg);

        Optional<HttpMsg> result = httpMsgRepository.findLastSuccessfulPercentageAPICall();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(httpMsg.getStatus(), result.get().getStatus());
        Assertions.assertEquals(httpMsg.getResponseBody(), result.get().getResponseBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        Assertions.assertEquals("11.0", testRestTemplate.exchange("/sum?value1=5.0&value2=5.0", HttpMethod.GET, requestEntity, String.class).getBody());
    }

}
