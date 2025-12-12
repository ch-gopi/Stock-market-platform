package com.market.historicalservice.consumer;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.historicalservice.dto.CandleDto;

import com.market.historicalservice.service.CandleAggregationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
@Service
public class FinQuoteTickConsumer {

    private final CandleAggregationService candleAggregationService;

    public FinQuoteTickConsumer(CandleAggregationService candleAggregationService) {
        this.candleAggregationService = candleAggregationService;
    }

    @KafkaListener(topics = "quotes.ticks", groupId = "historical-service")
    public void consume(FinQuoteTickEvent tick, ConsumerRecord<String, FinQuoteTickEvent> record) {
        long timestamp = tick.getTimestamp() != 0 ? tick.getTimestamp() : record.timestamp();
        tick.setTimestamp(timestamp); // normalize timestamp
        candleAggregationService.onTick(tick);
    }
}
