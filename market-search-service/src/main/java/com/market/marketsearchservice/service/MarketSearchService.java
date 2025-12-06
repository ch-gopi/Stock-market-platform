package com.market.marketsearchservice.service;

import com.market.marketsearchservice.dto.StockSearchDto;
import com.market.marketsearchservice.external.AlphaVantageClient;
import com.market.marketsearchservice.external.YahooFinanceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class MarketSearchService {

    private final AlphaVantageClient alphaVantageClient;
    private final YahooFinanceClient yahooFinanceClient;

    public MarketSearchService(AlphaVantageClient alphaVantageClient, YahooFinanceClient yahooFinanceClient) {
        this.alphaVantageClient = alphaVantageClient;
        this.yahooFinanceClient = yahooFinanceClient;
    }

    public List<StockSearchDto> searchStocks(String keyword) {

        Map<String, Object> response = alphaVantageClient.searchSymbol(keyword);

        if (response == null || !response.containsKey("bestMatches")) {
            return List.of();
        }

        List<Map<String, String>> matches =
                (List<Map<String, String>>) response.get("bestMatches");

        List<StockSearchDto> results = new ArrayList<>();

        for (Map<String, String> m : matches) {
            try {
                results.add(new StockSearchDto(
                        m.getOrDefault("1. symbol", ""),
                        m.getOrDefault("2. name", ""),
                        m.getOrDefault("3. type", ""),
                        m.getOrDefault("4. region", ""),
                        m.getOrDefault("5. marketOpen", ""),
                        m.getOrDefault("6. marketClose", ""),
                        m.getOrDefault("7. timezone", ""),
                        m.getOrDefault("8. currency", ""),
                        Double.parseDouble(m.getOrDefault("9. matchScore", "0.0"))
                ));
            } catch (Exception e) {
                log.warn("Skipping malformed match: {}", m, e);
            }
        }


        return results;
    }
}
