package com.ncs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class NcsProjectPublicService1Application {

	public static void main(String[] args) {
		SpringApplication.run(NcsProjectPublicService1Application.class, args);
	}

}
