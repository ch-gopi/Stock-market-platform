package com.market.watchlistservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "watchlist-service is running";
    }

    @GetMapping("/actuator/health")
    public String health() {
        return "OK from watchlist-service";
    }
}
