package com.market.historicalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient

@SpringBootApplication
public class historicalserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(historicalserviceApplication.class, args);
    }
}
