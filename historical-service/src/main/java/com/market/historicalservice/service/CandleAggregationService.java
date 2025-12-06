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
        CandleDto candle = new CandleDto(
                tick.getTimestamp(),
                tick.getPrice(), tick.getPrice(),
                tick.getPrice(), tick.getPrice(),
                tick.getVolume()
        );
        repository.save(tick.getSymbol(), candle);  // FIX: pass symbol + candle
    }

}
