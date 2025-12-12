package com.market.quotesservice.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class PreviousCloseUpdater {

    private static final Logger log = LoggerFactory.getLogger(PreviousCloseUpdater.class);

    private final QuoteCacheService quoteCacheService;
    private final RestTemplate restTemplate;
    private final String apiKey = "YOUR_FINNHUB_API_KEY";

    public PreviousCloseUpdater(QuoteCacheService quoteCacheService, RestTemplateBuilder builder) {
        this.quoteCacheService = quoteCacheService;
        this.restTemplate = builder.build();
    }

    // Run once every weekday morning before market opens
    @Scheduled(cron = "0 0 6 * * MON-FRI")
    public void updatePreviousClose() {
        List<String> symbols = List.of(
                "AAPL","MSFT","GOOGL","TSLA","AMZN","META","NFLX","NVDA",
                "AMD","INTC","IBM","ORCL","SAP","PYPL","SQ","BA","DIS",
                "NKE","SBUX","JPM","GS","BAC","WMT","COST","PEP","KO",
                "XOM","CVX","UNH","PFE"
        );

        for (String symbol : symbols) {
            String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", symbol, apiKey);
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Double previousClose = (Double) response.getBody().get("pc");
                    if (previousClose != null) {
                        quoteCacheService.savePreviousClose(symbol, previousClose);
                        log.info("Updated previous close for {}: {}", symbol, previousClose);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch previous close for {}", symbol, e);
            }
        }
    }
}
