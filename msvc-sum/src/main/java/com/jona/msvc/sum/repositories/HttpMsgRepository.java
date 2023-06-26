package com.jona.msvc.sum.repositories;

import com.jona.msvc.sum.models.entities.HttpMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HttpMsgRepository extends JpaRepository<HttpMsg, Integer> {

    @Query(value = "SELECT * FROM http_msg m WHERE m.status = 200 and m.response_body IS NOT NULL ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<HttpMsg> findLastSuccessfulPercentageAPICall();
}
