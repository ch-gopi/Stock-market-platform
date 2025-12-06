package com.market.watchlistservice.service;

import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.watchlistservice.external.AlphaVantageClient;
import com.market.watchlistservice.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WatchlistService {

    private final WatchlistRepository repo;
    private final AlphaVantageClient alphaClient;

    public WatchlistService(WatchlistRepository repo, AlphaVantageClient alphaClient) {
        this.repo = repo;
        this.alphaClient = alphaClient;
    }

    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        List<WatchlistEntry> entries = repo.findByUserId(userId);
        List<WatchlistItemDto> response = new ArrayList<>();

        for (WatchlistEntry e : entries) {
            response.add(fetchQuote(e.getSymbol()));
        }

        return response;
    }

    private WatchlistItemDto fetchQuote(String symbol) {
        Map<String, Object> resp = alphaClient.getQuote(symbol);

        if (resp == null || !resp.containsKey("Global Quote"))
            return new WatchlistItemDto(symbol, 0, 0, 0);

        Map<String, String> quote = (Map<String, String>) resp.get("Global Quote");

        try {
            double price = Double.parseDouble(quote.getOrDefault("05. price", "0"));
            double change = Double.parseDouble(quote.getOrDefault("09. change", "0"));
            double changePercent = Double.parseDouble(
                    quote.getOrDefault("10. change percent", "0").replace("%", ""));

            return new WatchlistItemDto(symbol, price, change, changePercent);
        } catch (Exception e) {
            return new WatchlistItemDto(symbol, 0, 0, 0);
        }
    }

    public void addToWatchlist(Long userId, String symbol) {
        repo.save(WatchlistEntry.builder()
                .userId(userId)
                .symbol(symbol)
                .build());
    }

    public void removeFromWatchlist(Long userId, String symbol) {
        repo.deleteByUserIdAndSymbol(userId, symbol);
    }
}
