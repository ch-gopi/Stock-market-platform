package com.market.marketsearchservice.config;



import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishEnrichmentEvent(String symbol) {
        String payload = String.format("{\"symbol\":\"%s\",\"action\":\"ENRICH\"}", symbol);
        kafkaTemplate.send("stock-enrichment", symbol, payload);
    }
}
