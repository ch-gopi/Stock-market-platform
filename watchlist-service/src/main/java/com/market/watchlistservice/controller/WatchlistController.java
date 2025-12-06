package com.market.watchlistservice.controller;

import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.watchlistservice.service.WatchlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistService service;

    public WatchlistController(WatchlistService service) {
        this.service = service;
    }


    @GetMapping("/{userId}")
    public List<WatchlistItemDto> getList(@PathVariable Long userId) {
        return service.getUserWatchlist(userId);
    }

    @PostMapping
    public String add(@RequestBody WatchlistEntry entry) {
        service.addToWatchlist(entry.getUserId(), entry.getSymbol().toUpperCase());
        return "Added";
    }

    @DeleteMapping
    public String delete(@RequestBody WatchlistEntry entry) {
        service.removeFromWatchlist(entry.getUserId(), entry.getSymbol().toUpperCase());
        return "Removed";
    }
}
