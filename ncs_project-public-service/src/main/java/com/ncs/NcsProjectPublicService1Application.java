package com.ncs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableEurekaClient
@Validated
public class NcsProjectPublicService1Application {

	public static void main(String[] args) {
		SpringApplication.run(NcsProjectPublicService1Application.class, args);
	}

}
