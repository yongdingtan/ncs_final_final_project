package com.ncs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@EnableEurekaClient
@Validated
public class NcsProjectApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NcsProjectApiGatewayApplication.class, args);
	}

}
