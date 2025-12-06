package com.market.historicalservice.service;

import com.market.historicalservice.dto.CandleDto;
import com.market.historicalservice.external.AlphaVantageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class HistoricalService {

    private final AlphaVantageClient alphaVantageClient;

    public HistoricalService(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }
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


    public List<CandleDto> getHistoricalData(String symbol, String range) {
        // Instead of calling AlphaVantageClient, return dummy candles
        List<CandleDto> candles = new ArrayList<>();

        // Simulate 5 days of data
        candles.add(new CandleDto(19620, 280.0, 285.0, 278.5, 282.0, 32000000L));
        candles.add(new CandleDto(19621, 282.0, 286.0, 280.0, 284.5, 31000000L));
        candles.add(new CandleDto(19622, 284.5, 287.0, 283.0, 285.0, 30000000L));
        candles.add(new CandleDto(19623, 285.0, 288.0, 284.0, 287.0, 29000000L));
        candles.add(new CandleDto(19624, 287.0, 289.0, 286.0, 288.5, 28000000L));

        return candles;
    }

}
