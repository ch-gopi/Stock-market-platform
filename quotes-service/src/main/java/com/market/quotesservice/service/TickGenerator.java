package com.market.quotesservice.service;



import com.market.quotesservice.dto.QuoteTickEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TickGenerator {
    private final TickPublisher publisher;
    private final List<String> symbols;
    private final Random rnd = new Random();

    public TickGenerator(TickPublisher publisher,
                         @Value("#{'${quotes.symbols:AAPL,MSFT,NVDA}'.split(',')}") List<String> symbols) {
        this.publisher = publisher;
        this.symbols = symbols;
    }


    @Scheduled(fixedDelayString = "${quotes.emit-interval-ms}")
    public void emitTicks() {
        long now = System.currentTimeMillis();
        for (String s : symbols) {
            double base = 180.0 + rnd.nextDouble() * 50.0;
            double price = Math.max(1.0, base + rnd.nextGaussian());
            double changeAbs = price - base;
            double changePct = base != 0 ? (changeAbs / base) * 100.0 : 0.0;
            long volume = 500_000L + rnd.nextInt(500_000);

            QuoteTickEvent event = QuoteTickEvent.builder()
                    .symbol(s)
                    .price(price)
                    .change(changeAbs)
                    .changePercent(changePct)
                    .volume(volume)
                    .timestamp(now)
                    .build();

            publisher.publish(event);
        }
    }
}
