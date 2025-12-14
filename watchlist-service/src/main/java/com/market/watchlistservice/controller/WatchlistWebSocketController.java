package com.market.watchlistservice.controller;


import com.market.watchlistservice.dto.WatchlistItemDto;
import com.market.watchlistservice.service.WatchlistService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WatchlistWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WatchlistService watchlistService;

    public WatchlistWebSocketController(SimpMessagingTemplate messagingTemplate,
                                        WatchlistService watchlistService) {
        this.messagingTemplate = messagingTemplate;
        this.watchlistService = watchlistService;
    }

    /**
     * REST endpoint to fetch a user's watchlist (for initial load).
     */
/*    @GetMapping("/watchlist/{userId}")
    public List<WatchlistItemDto> getWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }*/

    /**
     * Utility method to push updates to a specific user.
     * Called from NotificationService when a tick arrives.
     */
    public void pushWatchlistUpdate(Long userId, List<WatchlistItemDto> items) {
        messagingTemplate.convertAndSend("/topic/watchlist/" + userId, items);
    }
}
