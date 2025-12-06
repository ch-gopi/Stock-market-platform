package com.market.marketsearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@RefreshScope
@EnableRetry
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class marketsearchserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(marketsearchserviceApplication.class, args);
    }
}
