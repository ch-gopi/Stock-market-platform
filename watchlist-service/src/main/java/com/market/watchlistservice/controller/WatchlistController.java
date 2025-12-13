package com.market.watchlistservice.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.watchlistservice.service.WatchlistService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public List<WatchlistItemDto> getList(@PathVariable("userId") Long userId) {
        return service.getUserWatchlist(userId);
    }

    @PostMapping
    public WatchlistItemDto add(@RequestBody WatchlistEntry entry) {
        return service.addToWatchlist(entry.getUserId(), entry.getSymbol());
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestBody WatchlistEntry entry) {
        service.removeFromWatchlist(entry.getUserId(), entry.getSymbol().toUpperCase());
        return ResponseEntity.ok("Removed");
    }
}
