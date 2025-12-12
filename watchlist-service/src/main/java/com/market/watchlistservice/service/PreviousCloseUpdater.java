package com.market.watchlistservice.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PreviousCloseUpdater {

    private final QuoteCacheService quoteCacheService;
    private final RestTemplate restTemplate;
    private final String apiKey = "d4se4apr01qvsjbh6j00d4se4apr01qvsjbh6j0g";

    private final List<String> symbols = List.of(
            "AAPL", "MSFT", "GOOGL", "TSLA", "AMZN", "META", "NFLX", "NVDA",
            "AMD", "INTC", "IBM", "ORCL", "SAP", "PYPL", "SQ", "BA", "DIS",
            "NKE", "SBUX", "JPM", "GS", "BAC", "WMT", "COST", "PEP", "KO",
            "XOM", "CVX", "UNH", "PFE"
    ); // configure your watchlist

    public PreviousCloseUpdater(QuoteCacheService quoteCacheService, RestTemplateBuilder builder) {
        this.quoteCacheService = quoteCacheService;
        this.restTemplate = builder.build();
    }

    /**
     * Run once every weekday morning before market opens (6 AM IST).
     */
    @Scheduled(cron = "0 0 6 * * MON-FRI")
    public void updatePreviousClose() {
        log.info("Running scheduled PreviousCloseUpdater at 6 AM...");
        symbols.forEach(this::updateSingleSymbol);
    }

    /**
     * Run once at startup to ensure cache is populated even if service
     * wasn’t running at 6 AM.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing PreviousCloseUpdater on startup...");
        symbols.forEach(this::updateSingleSymbol);
    }

    /**
     * Fallback method: fetch previous close for a single symbol on demand.
     * Useful if cache lookup returns null.
     */
    public void updateSingleSymbol(String symbol) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, apiKey);
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Double previousClose = (Double) response.getBody().get("pc");
                if (previousClose != null) {
                    quoteCacheService.savePreviousClose(symbol, previousClose);
                    log.info("Updated previous close for {} = {}", symbol, previousClose);
                } else {
                    log.warn("No previous close value returned for {}", symbol);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch previous close for {}", symbol, e);
        }
    }
}
