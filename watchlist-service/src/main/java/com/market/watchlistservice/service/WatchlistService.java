package com.market.watchlistservice.service;

import com.market.watchlistservice.client.HistoricalClient;
import com.market.watchlistservice.dto.*;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.repository.WatchlistRepository;
import lombok.extern.slf4j.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Slf4j
@Service
public class WatchlistService {

    private final WatchlistRepository repo;
    private final QuoteCacheService quoteCache;
    private final HistoricalClient historicalClient;

    public WatchlistService(WatchlistRepository repo,
                            QuoteCacheService quoteCache,
                            HistoricalClient historicalClient) {
        this.repo = repo;
        this.quoteCache = quoteCache;
        this.historicalClient = historicalClient;
    }

    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(item -> buildDto(item.getSymbol()))
                .toList();
    }

    public WatchlistItemDto addToWatchlist(Long userId, String symbol) {
        String normalized = symbol.toUpperCase();

        repo.findByUserIdAndSymbolIgnoreCase(userId, normalized)
                .orElseGet(() -> {
                    WatchlistEntry entry = new WatchlistEntry();
                    entry.setUserId(userId);
                    entry.setSymbol(normalized);
                    repo.save(entry);
                    return entry;
                });
        log.info("Watchlist entry added to watchlist");

        //  Always return enriched DTO
        return buildDto(normalized);
    }

    public void removeFromWatchlist(Long userId, String symbol) {
        repo.findByUserId(userId).stream()
                .filter(i -> i.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .ifPresent(repo::delete);
        log.info("Watchlist entry removed from watchlist");
    }

    /** Helper to build a WatchlistItemDto with live quote and sparkline */
    private WatchlistItemDto buildDto(String symbol) {
        FinQuoteTickEvent tick = quoteCache.getLatestTick(symbol);
        double prevClose = quoteCache.getPreviousClose(symbol);

        // Circuit breaker applied here
        List<CandleDto> history = getHistoryWithCircuitBreaker(symbol);
        List<Double> sparkline = history.stream()
                .map(CandleDto::getClose)
                .toList();

        if (tick != null) {
            double lastPrice = tick.getPrice();
            double change = lastPrice - prevClose;
            double changePercent = prevClose != 0 ? (change / prevClose) * 100 : 0;

            return new WatchlistItemDto(
                    symbol,
                    lastPrice,
                    change,
                    changePercent,
                    sparkline
            );
        } else {
            return new WatchlistItemDto(symbol, 0, 0, 0, sparkline);
        }
    }

    @CircuitBreaker(name = "historical", fallbackMethod = "fallbackHistory")
    private List<CandleDto> getHistoryWithCircuitBreaker(String symbol) {
        return historicalClient.getHistory(symbol, "1m");
    }

    // Fallback method when historical service is unavailable
    private List<CandleDto> fallbackHistory(String symbol, Throwable t) {
        log.warn("Historical service unavailable for {}. Returning empty history.", symbol, t);
        return Collections.emptyList();
    }
}
