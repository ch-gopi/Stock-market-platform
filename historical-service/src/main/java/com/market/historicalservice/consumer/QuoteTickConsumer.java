package com.market.historicalservice.consumer;


import com.market.historicalservice.dto.QuoteTickEvent;
import com.market.historicalservice.service.CandleAggregationService;
import com.market.historicalservice.service.HistoricalService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class QuoteTickConsumer {
    private final HistoricalService historicalService;

    public QuoteTickConsumer(HistoricalService historicalService) {
        this.historicalService = historicalService;
    }
/*
    @KafkaListener(topics = "quotes.ticks", groupId = "historical-service")
    public void consume(QuoteTickEvent event) {
        historicalService.aggregateTick(event);
    }*/
}
