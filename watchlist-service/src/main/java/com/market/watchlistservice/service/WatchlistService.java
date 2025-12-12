package com.market.watchlistservice.service;

import com.market.watchlistservice.client.HistoricalClient;
import com.market.watchlistservice.dto.*;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        List<WatchlistEntry> items = repo.findByUserId(userId);
        List<WatchlistItemDto> response = new ArrayList<>();

        for (WatchlistEntry item : items) {
            response.add(buildDto(item.getSymbol()));
        }
        return response;
    }

    public WatchlistItemDto addToWatchlist(Long userId, String symbol) {
        WatchlistEntry entry = repo.findByUserId(userId).stream()
                .filter(e -> e.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElse(null);

        if (entry == null) {
            entry = new WatchlistEntry();
            entry.setUserId(userId);
            entry.setSymbol(symbol.toUpperCase());
            repo.save(entry);
        }

        return buildDto(entry.getSymbol());
    }

    public void removeFromWatchlist(Long userId, String symbol) {
        repo.findByUserId(userId).stream()
                .filter(i -> i.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .ifPresent(repo::delete);
    }

    /** Helper to build a WatchlistItemDto with live quote and sparkline */
    private WatchlistItemDto buildDto(String symbol) {
        FinQuoteTickEvent tick = quoteCache.getLatestTick(symbol);
        double prevClose = quoteCache.getPreviousClose(symbol);

        // Feign call to historical-service
        List<CandleDto> history = historicalClient.getHistory(symbol, "1m");
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
}
