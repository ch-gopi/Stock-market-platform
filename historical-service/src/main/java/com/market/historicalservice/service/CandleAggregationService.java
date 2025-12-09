package com.market.historicalservice.service;


import com.market.historicalservice.dto.CandleDto;
import com.market.historicalservice.dto.QuoteTickEvent;
import com.market.historicalservice.repository.CandleRepository;
import org.springframework.stereotype.Service;
@Service
public class CandleAggregationService {
    private final CandleRepository repository;

    public CandleAggregationService(CandleRepository repository) {
        this.repository = repository;
    }

    public void aggregate(QuoteTickEvent tick) {
        // Use tick fields directly instead of duplicating price
        CandleDto candle = new CandleDto(
                System.currentTimeMillis(),   // timestamp anchor
                tick.getOpen(),               // open
                tick.getHigh(),               // high
                tick.getLow(),                // low
                tick.getPrice(),              // close (latest price)
                tick.getVolume()              // volume
        );

        // Persist with symbol as key
        repository.save(tick.getSymbol(), candle);
    }
}
