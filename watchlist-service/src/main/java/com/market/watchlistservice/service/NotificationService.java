package com.market.watchlistservice.service;

import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.watchlistservice.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final WatchlistRepository repository;

    public NotificationService(WatchlistRepository repository) {
        this.repository = repository;
    }

    public void notifyUsers(QuoteTickEvent event) {
        var users = repository.findBySymbol(event.getSymbol());
        for (WatchlistEntry user : users) {
            log.info("Notify {}: {} price={}, change={}%", user,
                    event.getSymbol(), event.getPrice(), event.getChangePercent());
        }
    }

}
