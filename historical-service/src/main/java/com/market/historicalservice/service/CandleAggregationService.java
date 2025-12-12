package com.market.historicalservice.service;


import com.market.historicalservice.dto.CandleDto;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.historicalservice.dto.QuoteTickEvent;
import com.market.historicalservice.entity.CandleEntity;
import com.market.historicalservice.repository.CandleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class CandleAggregationService {

    private final CandleRepository candleRepository;

    // Buffer raw ticks per symbol
    private final Map<String, List<FinQuoteTickEvent>> tickBuffer = new ConcurrentHashMap<>();

    public CandleAggregationService(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    // Collect ticks from Kafka
    public void onTick(FinQuoteTickEvent tick) {
        tickBuffer.computeIfAbsent(tick.getSymbol(), k -> new ArrayList<>()).add(tick);
    }

    // Aggregate once per minute
    @Scheduled(fixedRate = 60000)
    public void aggregateCandles() {
        tickBuffer.forEach((symbol, ticks) -> {
            if (!ticks.isEmpty()) {
                double open = ticks.get(0).getPrice();
                double close = ticks.get(ticks.size() - 1).getPrice();
                double high = ticks.stream().mapToDouble(FinQuoteTickEvent::getPrice).max().orElse(open);
                double low = ticks.stream().mapToDouble(FinQuoteTickEvent::getPrice).min().orElse(open);
                double volume = ticks.stream().mapToDouble(FinQuoteTickEvent::getVolume).sum();

                CandleEntity candle = new CandleEntity();
                candle.setSymbol(symbol);
                candle.setOpen(open);
                candle.setClose(close);
                candle.setHigh(high);
                candle.setLow(low);
                candle.setVolume(volume);

                // Use bucket timestamp (truncate to minute)
                long bucketTimestamp = ticks.get(0).getTimestamp() / 60000 * 60000;
                candle.setTimestamp(bucketTimestamp);

                candleRepository.save(candle);
                ticks.clear();
            }
        });
    }

}

/*
    public void aggregate(FinQuoteTickEvent tick) {
        long timestamp = System.currentTimeMillis();

        CandleDto candle = new CandleDto(
                timestamp,
                tick.getOpen(),
                tick.getHigh(),
                tick.getLow(),
                tick.getPrice(),   // close
                tick.getVolume()
        );

        candleRepository.save(tick.getSymbol(), candle);
    }*/
