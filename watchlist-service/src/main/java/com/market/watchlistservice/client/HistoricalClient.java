package com.market.watchlistservice.client;

import com.market.watchlistservice.config.FeignConfig;
import com.market.watchlistservice.dto.CandleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name = "api-gateway", configuration = FeignConfig.class)
public interface HistoricalClient {
    @GetMapping("/historical/{symbol}")
    List<CandleDto> getHistory(@PathVariable("symbol") String symbol,
                               @RequestParam("range") String range);
}
