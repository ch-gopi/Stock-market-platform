package com.market.quotesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@RefreshScope
@EnableFeignClients
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class QuotesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuotesServiceApplication.class, args);
    }
}
