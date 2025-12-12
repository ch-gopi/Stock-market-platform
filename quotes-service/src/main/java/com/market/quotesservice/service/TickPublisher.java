/*
package com.market.quotesservice.service;



import com.market.quotesservice.dto.QuoteTickEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TickPublisher {
    private final KafkaTemplate<String, QuoteTickEvent> kafkaTemplate;
    private final String topic;

    public TickPublisher(KafkaTemplate<String, QuoteTickEvent> kafkaTemplate,
                         @Value("${quotes.ticks.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(QuoteTickEvent event) {
        kafkaTemplate.send(topic, event.getSymbol(), event);
    }
}
*/
