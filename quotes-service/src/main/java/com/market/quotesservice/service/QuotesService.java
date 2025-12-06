package com.market.quotesservice.service;


import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.external.AlphaVantageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class QuotesService {

    private final AlphaVantageClient alphaVantageClient;

    public QuotesService(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public QuoteDto getLiveQuote(String symbol) {
        Map<String, Object> response = alphaVantageClient.getIntraday(symbol);

        if (response == null || response.containsKey("Note") || response.containsKey("Error Message")) {
            log.warn("Intraday unavailable for {}: {}", symbol, response);
            return getDailyFallback(symbol);
        }

        if (!response.containsKey("Time Series (5min)")) {
            log.warn("No intraday data found for symbol: {}", symbol);
            return getDailyFallback(symbol);
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (5min)");
        TreeMap<String, Map<String, String>> sorted = new TreeMap<>((a, b) -> b.compareTo(a));
        for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
            sorted.put(entry.getKey(), (Map<String, String>) entry.getValue());
        }

        Map<String, String> latest = sorted.firstEntry().getValue();
        double open = Double.parseDouble(latest.getOrDefault("1. open", "0"));
        double close = Double.parseDouble(latest.getOrDefault("4. close", "0"));
        long volume = Long.parseLong(latest.getOrDefault("5. volume", "0"));

        double change = close - open;
        double changePercent = open != 0 ? (change / open) * 100 : 0;

        return new QuoteDto(symbol, close, change, changePercent, volume);
    }

    private QuoteDto getDailyFallback(String symbol) {
        Map<String, Object> response = alphaVantageClient.getDaily(symbol);

        if (response == null || !response.containsKey("Time Series (Daily)")) {
            log.error("No daily data found for symbol: {}", symbol);
            return new QuoteDto(symbol, 0, 0, 0, 0);
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
        TreeMap<String, Map<String, String>> sorted = new TreeMap<>((a, b) -> b.compareTo(a));
        for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
            sorted.put(entry.getKey(), (Map<String, String>) entry.getValue());
        }

        Map<String, String> latest = sorted.firstEntry().getValue();
        double open = Double.parseDouble(latest.getOrDefault("1. open", "0"));
        double close = Double.parseDouble(latest.getOrDefault("4. close", "0"));
        long volume = Long.parseLong(latest.getOrDefault("6. volume", "0"));

        double change = close - open;
        double changePercent = open != 0 ? (change / open) * 100 : 0;

        return new QuoteDto(symbol, close, change, changePercent, volume);
    }
}
