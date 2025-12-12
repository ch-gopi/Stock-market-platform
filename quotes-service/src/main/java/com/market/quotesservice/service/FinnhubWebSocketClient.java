package com.market.quotesservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.market.common.dto.FinQuoteTickEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class FinnhubWebSocketClient {

    private final KafkaTemplate<String, FinQuoteTickEvent> kafkaTemplate;
    private final Queue<String> symbols;
    private WebSocketClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RateLimiter limiter = RateLimiter.create(1.0); // 1 req/sec

    @Value("${finnhub.api.key}")
    private String apiKey;

    public FinnhubWebSocketClient(KafkaTemplate<String, FinQuoteTickEvent> kafkaTemplate,
                                  @Value("${quotes.symbols}") String symbolsCsv) {
        this.kafkaTemplate = kafkaTemplate;
        this.symbols = new LinkedList<>(Arrays.asList(symbolsCsv.split(",")));
    }

    @PostConstruct
    public void init() {
        System.out.println("▶️ Initializing FinnhubWebSocketClient with API key: " + apiKey);
        connect();
    }

    private void connect() {
        String uri = "wss://ws.finnhub.io?token=" + apiKey;
        System.out.println("Connecting to Finnhub at: " + uri);

        client = new WebSocketClient(URI.create(uri)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println(" Connected to Finnhub WebSocket");
                scheduler.scheduleAtFixedRate(() -> {
                    if (!symbols.isEmpty() && client.isOpen()) {
                        subscribe(symbols.poll());
                    }
                }, 0, 1, TimeUnit.SECONDS); // staggered subscriptions
            }

            @Override
            public void onMessage(String message) {
                try {
                    JsonNode root = mapper.readTree(message);
                    if ("trade".equals(root.get("type").asText())) {
                        for (JsonNode trade : root.get("data")) {
                            FinQuoteTickEvent tick = FinQuoteTickEvent.builder()
                                    .symbol(trade.get("s").asText())
                                    .price(trade.get("p").asDouble())
                                    .timestamp(trade.get("t").asLong())
                                    .volume(trade.get("v").asDouble())
                                    .build();
                            kafkaTemplate.send("quotes.ticks", tick.getSymbol(), tick);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error parsing message: " + message);
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("🔌 Disconnected: " + reason);
                scheduleReconnect();
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("⚠️ WebSocket error: " + ex.getMessage());
                scheduleReconnect();
            }

            private void subscribe(String symbol) {
                limiter.acquire(); // enforce 60/min
                String payload = String.format("{\"type\":\"subscribe\",\"symbol\":\"%s\"}", symbol.trim());
                if (client != null && client.isOpen()) {
                    client.send(payload);
                    System.out.println("📩 Subscribed to: " + symbol);
                }
            }
        };

        client.connect();
    }

    private void scheduleReconnect() {
        System.out.println("⏳ Scheduling reconnect in 30 seconds...");
        scheduler.schedule(this::connect, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("🛑 Shutting down FinnhubWebSocketClient...");
        if (client != null) {
            client.close();
        }
        scheduler.shutdown();
    }
}
