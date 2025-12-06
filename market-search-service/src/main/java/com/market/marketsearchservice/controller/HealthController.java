package com.market.marketsearchservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "market-search-service is running";
    }

    @GetMapping("/actuator/health")
    public String health() {
        return "OK from market-search-service";
    }
}
