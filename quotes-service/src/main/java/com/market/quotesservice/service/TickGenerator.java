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
                         @Value("#{'${quotes.symbols:AAPL,GOOGL,META,NFLX,AMD,INTC,IBM,ORCL,SAP,TSLA,MSFT,NVDA}'.split(',')}") List<String> symbols) {
        this.publisher = publisher;
        this.symbols = symbols;
    }

    @Scheduled(fixedDelayString = "${quotes.emit-interval-ms}")
    public void emitTicks() {
        String tradingDay = java.time.LocalDate.now().toString();

        for (String s : symbols) {
            double base = 180.0 + rnd.nextDouble() * 50.0;
            double open = base;
            double high = base + rnd.nextDouble() * 5.0;
            double low = base - rnd.nextDouble() * 5.0;
            double price = Math.max(1.0, base + rnd.nextGaussian());
            double previousClose = base - rnd.nextDouble() * 2.0;
            double changeAbs = price - previousClose;
            String changePct = String.format("%.2f%%", (changeAbs / previousClose) * 100.0);
            long volume = 500_000L + rnd.nextInt(500_000);

            QuoteTickEvent event = QuoteTickEvent.builder()
                    .symbol(s)
                    .open(open)
                    .high(high)
                    .low(low)
                    .price(price)
                    .previousClose(previousClose)
                    .change(changeAbs)
                    .changePercent(changePct)
                    .volume(volume)
                    .latestTradingDay(tradingDay)
                    .build();

            publisher.publish(event);
        }
    }
}
