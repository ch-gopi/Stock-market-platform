package com.market.marketsearchservice.kafka;



import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic stockEnrichmentTopic() {
        return new NewTopic("stock-enrichment", 3, (short) 1);
    }
}
