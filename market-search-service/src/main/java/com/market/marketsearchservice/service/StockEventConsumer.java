package com.market.marketsearchservice.service;


import com.market.marketsearchservice.client.HistoricalClient;
import com.market.marketsearchservice.client.QuotesClient;
import com.market.marketsearchservice.dto.CandleDto;
import com.market.marketsearchservice.dto.QuoteDto;
import com.market.marketsearchservice.entity.StockMeta;
import com.market.marketsearchservice.repository.StockMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class StockEventConsumer {

    private final QuotesClient quotesClient;
    private final HistoricalClient historicalClient;
    private final StockMetaRepository stockMetaRepository;

    public StockEventConsumer(QuotesClient quotesClient,
                              HistoricalClient historicalClient,
                              StockMetaRepository stockMetaRepository) {
        this.quotesClient = quotesClient;
        this.historicalClient = historicalClient;
        this.stockMetaRepository = stockMetaRepository;
    }

    @KafkaListener(topics = "stock-enrichment", groupId = "market-search")
    public void consume(String message) {
        String symbol = extractSymbol(message);
        if (symbol == null || symbol.isBlank()) return;

        StockMeta meta = stockMetaRepository.findById(symbol).orElse(null);
        if (meta == null) return;

        try {
            QuoteDto quote = quotesClient.getQuote(symbol);
            meta.setPrice(quote.getPrice());
            meta.setChange(quote.getChange());
            meta.setChangePercent(quote.getChangePercent());
            meta.setVolume(quote.getVolume());

            List<CandleDto> candles = historicalClient.getCandles(symbol, "1mo");
            meta.setHistoricalPerformance(calculatePerformance(candles));

            stockMetaRepository.save(meta);
            log.info("Enriched and saved {}", symbol);
        } catch (Exception e) {
            log.warn("Failed enrichment for {}: {}", symbol, e.getMessage());
        }
    }

    private String extractSymbol(String message) {
        // naive extraction; replace with Jackson ObjectMapper in production
        try {
            return message.replaceAll(".*\"symbol\":\"(.*?)\".*", "$1");
        } catch (Exception e) {
            return null;
        }
    }

    private double calculatePerformance(List<CandleDto> candles) {
        if (candles == null || candles.size() < 2) return 0;
        double start = candles.get(0).getClose();
        double end = candles.get(candles.size() - 1).getClose();
        return start != 0 ? ((end - start) / start) * 100 : 0;
    }
}
