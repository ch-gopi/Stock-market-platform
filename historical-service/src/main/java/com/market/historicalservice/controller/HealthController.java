package com.market.historicalservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "historical-service is running";
    }

    @GetMapping("/actuator/health")
    public String health() {
        return "OK from historical-service";
    }
}
