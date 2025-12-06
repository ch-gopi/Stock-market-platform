package com.market.historicalservice.repository;

import com.market.historicalservice.dto.CandleDto;
import org.springframework.stereotype.Repository;

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
        // Simplified: return last N candles based on range
        int count = switch (range) {
            case "1d" -> 1;
            case "5d" -> 5;
            case "1mo" -> 20;
            default -> 10;
        };
        return all.stream().skip(Math.max(0, all.size() - count)).toList();
    }
}
