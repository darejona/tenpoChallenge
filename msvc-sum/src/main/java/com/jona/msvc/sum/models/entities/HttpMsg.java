package com.jona.msvc.sum.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "http_msg")
@Getter
@Setter
public class HttpMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String method;
    private String url;
    private String requestHeaders;
    private String responseHeaders;
    private String requestBody;
    private String responseBody;
    private int status;
}
