package com.market.historicalservice.repository;

import com.market.historicalservice.dto.CandleDto;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CandleRepository {
    private final Map<String, List<CandleDto>> store = new ConcurrentHashMap<>();

    public void save(String symbol, CandleDto candle) {
        store.computeIfAbsent(symbol, k -> new ArrayList<>()).add(candle);
    }
    public List<CandleDto> findBySymbolAndRange(String symbol, String range) {
        List<CandleDto> all = store.getOrDefault(symbol, List.of());
        long cutoff = resolveCutoff(range);

        return all.stream()
                .filter(c -> c.getTimestamp() >= cutoff)
                .toList();
    }
    private long resolveCutoff(String range) {
        long now = System.currentTimeMillis();
        return switch (range) {
            case "1d" -> now - Duration.ofDays(1).toMillis();
            case "5d" -> now - Duration.ofDays(5).toMillis();
            case "1mo" -> now - Duration.ofDays(30).toMillis();
            default -> now - Duration.ofDays(10).toMillis();
        };
    }
    private long alignToMinute(long timestamp) {
        return timestamp - (timestamp % 60000); // floor to minute
    }


}
