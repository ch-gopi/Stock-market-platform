package com.market.watchlistservice.service;

import com.market.watchlistservice.controller.WatchlistWebSocketController;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.watchlistservice.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final WatchlistRepository repository;
    private final WatchlistService watchlistService;
    private final WatchlistWebSocketController wsController;

    public NotificationService(WatchlistRepository repository,
                               WatchlistService watchlistService,
                               WatchlistWebSocketController wsController) {
        this.repository = repository;
        this.watchlistService = watchlistService;
        this.wsController = wsController;
    }

    public void notifyUsers(FinQuoteTickEvent event) {
        List<WatchlistEntry> users = repository.findBySymbol(event.getSymbol());

        for (WatchlistEntry entry : users) {
            List<WatchlistItemDto> items = watchlistService.getUserWatchlist(entry.getUserId());

            //  Push to WebSocket topic
            wsController.pushWatchlistUpdate(entry.getUserId(), items);

            log.info("📡 Notified user={} for symbol={} | price={}",
                    entry.getUserId(), event.getSymbol(), event.getPrice());
        }
    }
}
