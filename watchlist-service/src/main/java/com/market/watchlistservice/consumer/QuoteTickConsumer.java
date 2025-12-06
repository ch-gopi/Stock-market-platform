package com.market.watchlistservice.consumer;

import com.market.watchlistservice.dto.QuoteTickEvent;
import com.market.watchlistservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuoteTickConsumer {
    private final NotificationService notificationService;

    public QuoteTickConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "quotes.ticks", groupId = "watchlist-service")
    public void consume(QuoteTickEvent event) {
        notificationService.notifyUsers(event);
    }
}
