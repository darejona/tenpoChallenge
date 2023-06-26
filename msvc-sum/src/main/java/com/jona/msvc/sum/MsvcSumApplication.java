package com.jona.msvc.sum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class MsvcSumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcSumApplication.class, args);
	}

}
