package com.market.quotesservice.consumer;

import com.market.common.dto.FinQuoteTickEvent;   // from quotes-common
import com.market.quotesservice.dto.QuoteDto;     // new class you just created
import com.market.quotesservice.service.NotificationService;
import com.market.quotesservice.service.QuoteCacheService; // new service you just created

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;




@Component
public class QuoteConsumer {

    private static final Logger log = LoggerFactory.getLogger(QuoteConsumer.class);

    private final QuoteCacheService quoteCacheService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public QuoteConsumer(QuoteCacheService quoteCacheService,
                         SimpMessagingTemplate messagingTemplate,NotificationService notificationService) {
        this.quoteCacheService = quoteCacheService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;

    }

    @KafkaListener(topics = "quotes.ticks", groupId = "quotes-service",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(FinQuoteTickEvent event) {
        log.info("Consumed tick: {}", event);

        // Store in Redis
        quoteCacheService.saveLatestTick(event);
        notificationService.notifyUsers(event);

        // Compute change metrics
        double prevClose = quoteCacheService.getPreviousClose(event.getSymbol());
        double change = event.getPrice() - prevClose;
        double changePercent = prevClose != 0 ? (change / prevClose) * 100 : 0;


        QuoteDto dto = new QuoteDto(
                event.getSymbol(),
                event.getPrice(),
                change,
                changePercent,
                event.getVolume()
        );

        // Push immediately to frontend subscribers
        messagingTemplate.convertAndSend("/topic/quotes", dto);
    }
}
