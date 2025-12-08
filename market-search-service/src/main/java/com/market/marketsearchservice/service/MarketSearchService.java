package com.market.marketsearchservice.service;

import com.market.marketsearchservice.dto.CandleDto;
import com.market.marketsearchservice.dto.QuoteDto;
import com.market.marketsearchservice.dto.StockSearchDto;

import com.market.marketsearchservice.entity.StockMeta;
import com.market.marketsearchservice.external.AlphaVantageClient;
import com.market.marketsearchservice.repository.StockMetaRepository;
import com.market.marketsearchservice.client.QuotesClient;
import com.market.marketsearchservice.client.HistoricalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MarketSearchService {

    private static final Logger log = LoggerFactory.getLogger(MarketSearchService.class);

    private final AlphaVantageClient alphaVantageClient;

    public MarketSearchService(
                               AlphaVantageClient alphaVantageClient
                              ) {

        this.alphaVantageClient = alphaVantageClient;

    }

    public List<StockSearchDto> searchStocks(String keyword) {
        Map<String, Object> response = alphaVantageClient.searchSymbol(keyword);

        if (response == null || !response.containsKey("bestMatches")) {
            return List.of();
        }

        List<Map<String, String>> matches = (List<Map<String, String>>) response.get("bestMatches");

        return matches.stream()
                .filter(match -> {
                    String symbol = match.get("1. symbol");
                    // allow exact match OR starts with "AAPL."
                    return symbol.equalsIgnoreCase(keyword) || symbol.startsWith(keyword + ".");
                })
                .map(match -> new StockSearchDto(
                        match.get("1. symbol"),
                        match.get("2. name"),
                        match.get("3. type"),
                        match.get("4. region"),
                        match.get("5. marketOpen"),
                        match.get("6. marketClose"),
                        match.get("7. timezone"),
                        match.get("8. currency"),
                        Double.parseDouble(match.get("9. matchScore"))
                ))
                .toList();
    }


/*
    private double calculatePerformance(List<CandleDto> candles) {
        if (candles == null || candles.size() < 2) return 0;
        double start = candles.get(0).getClose();
        double end = candles.get(candles.size() - 1).getClose();
        return start != 0 ? ((end - start) / start) * 100 : 0;
    }*/
}
/*
    // Optional sync enrichment (use sparingly)
    public void enrichNow(String symbol, String range) {
        StockMeta meta = stockMetaRepository.findById(symbol).orElse(null);
        if (meta == null) return;

        try {
            QuoteDto quote = quotesClient.getQuote(symbol);
            if (quote != null) {
                meta.setPrice(quote.getPrice());
                meta.setChange(quote.getChange());
                meta.setChangePercent(quote.getChangePercent());
                meta.setVolume(quote.getVolume());
            }

            List<CandleDto> candles = historicalClient.getCandles(symbol, range);
            if (candles != null && !candles.isEmpty()) {
                meta.setHistoricalPerformance(calculatePerformance(candles));
            }

            stockMetaRepository.save(meta);
        } catch (Exception e) {
            log.warn("Sync enrichment failed for {}: {}", symbol, e.getMessage());
        }
    }

    private double calculatePerformance(List<CandleDto> candles) {
        if (candles == null || candles.size() < 2) return 0;
        double start = candles.get(0).getClose();
        double end = candles.get(candles.size() - 1).getClose();
        return start != 0 ? ((end - start) / start) * 100 : 0;
    }*/

