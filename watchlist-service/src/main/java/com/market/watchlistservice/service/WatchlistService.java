package com.market.watchlistservice.service;

import com.market.watchlistservice.client.QuotesClient;
import com.market.watchlistservice.dto.QuoteDto;
import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;

import com.market.watchlistservice.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository repo;
    private QuotesClient quotesClient;

    public WatchlistService(WatchlistRepository repo, QuotesClient quotesClient) {
        this.repo = repo;
        this.quotesClient = quotesClient;
    }

    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        List<WatchlistEntry> items = repo.findByUserId(userId);
        List<WatchlistItemDto> response = new ArrayList<>();
        for (WatchlistEntry item : items) {
            QuoteDto quote = quotesClient.getQuote(item.getSymbol());
            response.add(new WatchlistItemDto(
                    item.getSymbol(),
                    quote.getPrice(),
                    quote.getChange(),
                    quote.getChangePercent()));
        }
        return response;
    }

    public void addToWatchlist(Long userId, String symbol) {
        WatchlistEntry entry = new WatchlistEntry();
        entry.setUserId(userId);
        entry.setSymbol(symbol);
        repo.save(entry);
    }

    public void removeFromWatchlist(Long userId, String symbol) {
        List<WatchlistEntry> items = repo.findByUserId(userId);
        items.stream()
                .filter(i -> i.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .ifPresent(repo::delete);
    }
}
