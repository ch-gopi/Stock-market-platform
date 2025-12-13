package com.market.quotesservice.service;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.quotesservice.dto.QuoteDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QuoteBroadcaster {

    private final QuoteCacheService quoteCacheService;
    private final SimpMessagingTemplate messagingTemplate;

    public QuoteBroadcaster(QuoteCacheService quoteCacheService,
                            SimpMessagingTemplate messagingTemplate) {
        this.quoteCacheService = quoteCacheService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 1000) // every second
    public void broadcastCachedQuotes() {
        Map<String, FinQuoteTickEvent> latestTicks = quoteCacheService.getAllLatestTicks();
        latestTicks.values().forEach(tick -> {
            double prevClose = quoteCacheService.getPreviousClose(tick.getSymbol());
            double change = tick.getPrice() - prevClose;
            double changePercent = prevClose != 0 ? (change / prevClose) * 100 : 0;

            QuoteDto dto = new QuoteDto(
                    tick.getSymbol(),
                    tick.getPrice(),
                    change,
                    changePercent,
                    tick.getVolume()
            );

            messagingTemplate.convertAndSend("/topic/quotes", dto);
        });
    }
}
