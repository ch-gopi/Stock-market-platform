package com.market.marketsearchservice.client;

import java.util.List;

import com.market.marketsearchservice.dto.CandleDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "historical-service")

public interface HistoricalClient {
    @GetMapping("/historical/{symbol}")
    List<CandleDto> getCandles(@PathVariable("symbol") String symbol,
                               @RequestParam("range") String range);
}

