package com.market.watchlistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@RefreshScope
@EnableFeignClients
@SpringBootApplication
@EnableScheduling
public class watchlistserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(watchlistserviceApplication.class, args);
    }
}
