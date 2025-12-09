package com.market.watchlistservice.service;

import com.market.watchlistservice.client.QuotesClient;
import com.market.watchlistservice.dto.QuoteDto;
import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;

import com.market.watchlistservice.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class WatchlistService {

    private final WatchlistRepository repo;
    private final QuoteCache quoteCache; // Redis-backed cache
// Redis + Feign

    public WatchlistService(WatchlistRepository repo, QuoteCache quoteCache) {
        this.repo = repo;
        this.quoteCache = quoteCache;

    }

    // ✅ Get watchlist by userId
    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        List<WatchlistEntry> items = repo.findByUserId(userId);


        List<WatchlistItemDto> response = new ArrayList<>();
        for (WatchlistEntry item : items) {
            QuoteTickEvent tick = quoteCache.getLatest(item.getSymbol());
            if (tick != null) {
                response.add(new WatchlistItemDto(
                        item.getSymbol(),
                        tick.getPrice(),
                        tick.getChange(),
                        parseChangePercent(tick.getChangePercent())
                ));
            } else {
                // fallback if no tick yet
                response.add(new WatchlistItemDto(item.getSymbol(), 0, 0, 0));
            }
        }
        return response;
    }

    // ✅ Add symbol to watchlist by userId
    public void addToWatchlist(Long userId, String symbol) {
        WatchlistEntry entry = new WatchlistEntry();
        entry.setUserId(userId);
        entry.setSymbol(symbol);
        repo.save(entry);
    }

    // ✅ Remove symbol from watchlist by userId
    public void removeFromWatchlist(Long userId, String symbol) {
        List<WatchlistEntry> items = repo.findByUserId(userId);
        items.stream()
                .filter(i -> i.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .ifPresent(repo::delete);
    }

    private double parseChangePercent(String changePercent) {
        if (changePercent == null || changePercent.isBlank()) return 0.0;
        try {
            return Double.parseDouble(changePercent.replace("%", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
