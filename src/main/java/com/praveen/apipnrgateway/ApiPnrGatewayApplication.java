package com.praveen.apipnrgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiPnrGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPnrGatewayApplication.class, args);
	}

}
