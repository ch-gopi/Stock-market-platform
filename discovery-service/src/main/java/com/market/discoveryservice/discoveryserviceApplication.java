package com.market.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class discoveryserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(discoveryserviceApplication.class, args);
    }
}
