package com.market.watchlistservice.consumer;

import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.service.NotificationService;
import com.market.watchlistservice.service.QuoteCache;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
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
