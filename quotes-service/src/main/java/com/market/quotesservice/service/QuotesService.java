package com.market.quotesservice.service;


import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.external.AlphaVantageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class QuotesService {

    private final AlphaVantageClient alphaVantageClient;

    public QuotesService(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public QuoteDto getLiveQuote(String symbol) {
        Map<String, Object> response = alphaVantageClient.getQuote(symbol);

        if (response == null || !response.containsKey("Global Quote")) {
            log.warn("Global Quote unavailable for {}: {}", symbol, response);
            return new QuoteDto(symbol, 0, 0, 0, 0);
        }

        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");

        try {
            double price = Double.parseDouble(quote.getOrDefault("05. price", "0"));
            double change = Double.parseDouble(quote.getOrDefault("09. change", "0"));
            double changePercent = Double.parseDouble(
                    quote.getOrDefault("10. change percent", "0").replace("%", "")
            );
            long volume = Long.parseLong(quote.getOrDefault("06. volume", "0"));

            return new QuoteDto(symbol, price, change, changePercent, volume);
        } catch (Exception e) {
            log.error("Error parsing quote for {}: {}", symbol, e.getMessage());
            return new QuoteDto(symbol, 0, 0, 0, 0);
        }
    }
}
