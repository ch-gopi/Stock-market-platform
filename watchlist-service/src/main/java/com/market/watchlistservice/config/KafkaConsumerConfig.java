package com.market.watchlistservice.config;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.dto.QuoteTickEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, FinQuoteTickEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "watchlist-service"); // 👈 unique group id
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.class);

        config.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
        config.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, FinQuoteTickEvent.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FinQuoteTickEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FinQuoteTickEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
