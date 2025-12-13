package com.market.quotesservice.service;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.quotesservice.dto.QuoteDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Notify all subscribers with a QuoteDto update.
     */
    public void notifyQuoteUpdate(QuoteDto dto) {
        try {
            messagingTemplate.convertAndSend("/topic/quotes", dto);
            log.info("📢 Notified subscribers with update for {}: price={}, change={}, changePercent={}, volume={}",
                    dto.getSymbol(), dto.getPrice(), dto.getChange(), dto.getChangePercent(), dto.getVolume());
        } catch (Exception e) {
            log.error("❌ Failed to notify subscribers for {}: {}", dto.getSymbol(), e.getMessage(), e);
        }
    }

    /**
     * Notify all subscribers with a raw tick event (optional).
     */
    public void notifyUsers(FinQuoteTickEvent tick) {
        try {
            messagingTemplate.convertAndSend("/topic/quotes/raw", tick);
            log.info("📢 Notified subscribers with raw tick for {}: {}", tick.getSymbol(), tick);
        } catch (Exception e) {
            log.error("❌ Failed to notify subscribers with raw tick for {}: {}", tick.getSymbol(), e.getMessage(), e);
        }
    }

}
