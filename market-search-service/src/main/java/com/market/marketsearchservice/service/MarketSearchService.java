package com.market.marketsearchservice.service;

import com.market.marketsearchservice.dto.CandleDto;
import com.market.marketsearchservice.dto.QuoteDto;
import com.market.marketsearchservice.dto.StockSearchDto;

import com.market.marketsearchservice.entity.StockMeta;
import com.market.marketsearchservice.repository.StockMetaRepository;
import com.market.marketsearchservice.client.QuotesClient;
import com.market.marketsearchservice.client.HistoricalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MarketSearchService {

    private static final Logger log = LoggerFactory.getLogger(MarketSearchService.class);

    private final StockMetaRepository stockMetaRepository;
    private final QuotesClient quotesClient;
    private final HistoricalClient historicalClient;

    public MarketSearchService(StockMetaRepository stockMetaRepository,
                               QuotesClient quotesClient,
                               HistoricalClient historicalClient) {
        this.stockMetaRepository = stockMetaRepository;
        this.quotesClient = quotesClient;
        this.historicalClient = historicalClient;
    }

    public List<StockSearchDto> searchStocks(String keyword) {
        List<StockMeta> metas = stockMetaRepository.findBySymbolContainingIgnoreCase(keyword);
        return metas.stream()
                .map(meta -> new StockSearchDto(
                        meta.getSymbol(),
                        meta.getName(),
                        meta.getType(),
                        meta.getRegion(),
                        meta.getMarketOpen(),
                        meta.getMarketClose(),
                        meta.getTimezone(),
                        meta.getCurrency(),
                        meta.getMatchScore(),
                        meta.getPrice(),
                        meta.getChange(),
                        meta.getChangePercent(),
                        meta.getVolume(),
                        meta.getHistoricalPerformance()
                ))
                .toList();
    }


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
    }
}
