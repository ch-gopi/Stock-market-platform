package com.market.historicalservice.service;

import com.market.historicalservice.dto.CandleDto;

import com.market.historicalservice.dto.QuoteTickEvent;
import com.market.historicalservice.repository.CandleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class HistoricalService {


/*
    public List<CandleDto> getHistoricalData(String symbol, String range) {
        Map<String, Object> response = alphaVantageClient.getDaily(symbol);

        if (response == null || !response.containsKey("Time Series (Daily)")) {
            log.error("No daily data found for symbol: {}", symbol);
            return List.of();
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
        TreeMap<LocalDate, Map<String, String>> sorted = new TreeMap<>(Comparator.reverseOrder());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey(), formatter);
            sorted.put(date, (Map<String, String>) entry.getValue());
        }

        // 🔑 Range cutoff logic
        LocalDate cutoff = switch (range) {
            case "1mo" -> LocalDate.now().minusMonths(1);
            case "6mo" -> LocalDate.now().minusMonths(6);
            case "1y"  -> LocalDate.now().minusYears(1);
            default    -> LocalDate.now().minusMonths(1);
        };

        List<CandleDto> candles = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, String>> entry : sorted.entrySet()) {
            if (entry.getKey().isBefore(cutoff)) break;

            Map<String, String> data = entry.getValue();
            candles.add(new CandleDto(
                    entry.getKey().toEpochDay(),
                    Double.parseDouble(data.getOrDefault("1. open", "0")),
                    Double.parseDouble(data.getOrDefault("2. high", "0")),
                    Double.parseDouble(data.getOrDefault("3. low", "0")),
                    Double.parseDouble(data.getOrDefault("4. close", "0")),
                    Long.parseLong(data.getOrDefault("6. volume", "0"))
            ));
        }

        return candles;
    }*/

    private final CandleRepository candleRepository;

    public HistoricalService(CandleRepository repository, CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }
/*
    public List<CandleDto> getHistoricalData(String symbol, String range) {
        return candleRepository.findBySymbolAndRange(symbol, range);
    }*/

/*    public void aggregateTick(QuoteTickEvent tick) {
        // Use trading day as timestamp anchor
        long timestamp = System.currentTimeMillis();

        // Build candle using tick fields
        CandleDto candle = new CandleDto(
                timestamp,
                tick.getOpen(),          // open
                tick.getHigh(),          // high
                tick.getLow(),           // low
                tick.getPrice(),         // close (latest price)
                tick.getVolume()         // volume
        );

        candleRepository.save(tick.getSymbol(), candle);
    }*/

    public List<CandleDto> getCandles(String symbol, String range) {
        long now = Instant.now().toEpochMilli();
        long from;

        switch (range) {

                case "1d":
                    from = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "1w":
                    from = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "1m":
                    from = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "3m":
                    from = Instant.now().minus(90, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "6m":
                    from = Instant.now().minus(180, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "1y":
                    from = Instant.now().minus(365, ChronoUnit.DAYS).toEpochMilli();
                    break;
                case "5y":
                    from = Instant.now().minus(5, ChronoUnit.YEARS).toEpochMilli();
                    break;
                default:
                    from = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
            }


            return candleRepository.findBySymbolAndTimestampBetween(symbol, from, now);
    }

}

/*

    public List<CandleDto> getHistoricalData(String symbol, String range) {
        // Instead of calling AlphaVantageClient, return dummy candles
        List<CandleDto> candles = new ArrayList<>();

        // Simulate 5 days of data
        candles.add(new CandleDto(19620, 280.0, 285.0, 278.5, 282.0, 32000000L));
        candles.add(new CandleDto(19621, 282.0, 286.0, 280.0, 284.5, 31000000L));
        candles.add(new CandleDto(19622, 284.5, 287.0, 283.0, 285.0, 30000000L));
        candles.add(new CandleDto(19623, 285.0, 288.0, 284.0, 287.0, 29000000L));
        candles.add(new CandleDto(19624, 287.0, 289.0, 286.0, 288.5, 28000000L));
        candles.add(new CandleDto(19625, 288.5, 291.0, 287.0, 290.0, 27000000L));
        candles.add(new CandleDto(19626, 290.0, 293.0, 289.0, 292.5, 26000000L));
        candles.add(new CandleDto(19627, 292.5, 294.0, 291.0, 293.0, 25000000L));
        candles.add(new CandleDto(19628, 293.0, 295.0, 292.0, 294.5, 24000000L));
        candles.add(new CandleDto(19629, 294.5, 296.0, 293.0, 295.0, 23000000L));
        candles.add(new CandleDto(19630, 295.0, 297.0, 294.0, 296.5, 22000000L));
        candles.add(new CandleDto(19631, 296.5, 298.0, 295.0, 297.0, 21000000L));
        candles.add(new CandleDto(19632, 297.0, 299.0, 296.0, 298.5, 20000000L));
        candles.add(new CandleDto(19633, 298.5, 300.0, 297.0, 299.0, 19000000L));
        candles.add(new CandleDto(19634, 299.0, 301.0, 298.0, 300.5, 18000000L));
        candles.add(new CandleDto(19635, 300.5, 302.0, 299.0, 301.0, 17000000L));
        candles.add(new CandleDto(19636, 301.0, 303.0, 300.0, 302.5, 16000000L));
        candles.add(new CandleDto(19637, 302.5, 304.0, 301.0, 303.0, 15000000L));
        candles.add(new CandleDto(19638, 303.0, 305.0, 302.0, 304.5, 14000000L));
        candles.add(new CandleDto(19639, 304.5, 306.0, 303.0, 305.0, 13000000L));

        return candles;
    }

}
*/
