package com.market.historicalservice.repository;

import com.market.historicalservice.dto.CandleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CandleRepositoryTest {

    @Autowired
    private CandleRepository candleRepository;

    @Test
    void testFindBySymbolAndTimestampBetween() {
        long from = 1763359966292L;
        long to   = 1765951966292L;

        List<CandleDto> candles =
                candleRepository.findBySymbolAndTimestampBetween("AAPL", from, to);

        assertFalse(candles.isEmpty(), "Expected candles but got empty list");
        System.out.println("Retrieved rows: " + candles.size());
    }

}