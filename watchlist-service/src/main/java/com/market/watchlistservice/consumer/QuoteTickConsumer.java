package com.market.watchlistservice.consumer;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.service.NotificationService;

import com.market.watchlistservice.service.QuoteCacheService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuoteTickConsumer {

    private final QuoteCacheService quoteCacheService;
    private final NotificationService notificationService;

    public QuoteTickConsumer(QuoteCacheService quoteCacheService,
                             NotificationService notificationService) {
        this.quoteCacheService = quoteCacheService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "quotes.ticks", groupId = "watchlist-service")
    public void consume(FinQuoteTickEvent event) {
        try {
            //  Store in Redis
            quoteCacheService.saveLatestTick(event);

            //  Notify users who track this symbol
            notificationService.notifyUsers(event);
        } catch (Exception e) {
            // Prevent consumer crash
            System.err.println("❌ Failed to process tick for " + event.getSymbol() + ": " + e.getMessage());
        }
    }
}


/*
@Component
public class QuoteTickConsumer {
    private final QuoteCache quoteCache;
    private final NotificationService notificationService;

    public QuoteTickConsumer(QuoteCache quoteCache, NotificationService notificationService) {
        this.quoteCache = quoteCache;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "quotes.ticks", groupId = "watchlist-service")
    public void consume(QuoteTickEvent event) {
        // store in Redis
        quoteCache.update(event);

        // notify users
        notificationService.notifyUsers(event);
    }
}
*/
