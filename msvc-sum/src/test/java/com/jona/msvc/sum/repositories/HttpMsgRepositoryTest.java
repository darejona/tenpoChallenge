package com.jona.msvc.sum.repositories;

import com.jona.msvc.sum.models.entities.HttpMsg;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase( type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES, provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
public class HttpMsgRepositoryTest {

    @Autowired
    private HttpMsgRepository httpMsgRepository;

    @Test
    public void testFindLastSuccessfulPercentageAPICall() {
        HttpMsg httpMsg = new HttpMsg();
        httpMsg.setStatus(200);
        httpMsg.setResponseBody("10");
        httpMsgRepository.save(httpMsg);

        Optional<HttpMsg> result = httpMsgRepository.findLastSuccessfulPercentageAPICall();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(httpMsg.getStatus(), result.get().getStatus());
        Assertions.assertEquals(httpMsg.getResponseBody(), result.get().getResponseBody());
    }
}

