package com.cyvation.webclound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableEurekaServer
@SpringBootApplication
public class WebcloundApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcloundApplication.class, args);
	}
}
