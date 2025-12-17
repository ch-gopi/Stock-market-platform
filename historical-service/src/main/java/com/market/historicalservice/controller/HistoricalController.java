package com.market.historicalservice.controller;

import com.market.historicalservice.dto.CandleDto;
import com.market.historicalservice.service.HistoricalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/historical")

public class HistoricalController {

    private final HistoricalService historicalService;

    public HistoricalController(HistoricalService historicalService) {
        this.historicalService = historicalService;
    }
    @GetMapping("/{symbol}")
    public ResponseEntity<List<CandleDto>> getHistoricalData(
            @PathVariable(name = "symbol") String symbol,
            @RequestParam(name = "range", defaultValue = "1m") String range) {

        List<CandleDto> candles =
                historicalService.getCandles(symbol, range);

        return ResponseEntity.ok(candles);
    }
}
