package com.market.watchlistservice.client;

import com.market.watchlistservice.dto.QuoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "quotes-service")
public interface QuotesClient {
    @GetMapping("/quotes/{symbol}")
    QuoteDto getQuote(@PathVariable("symbol") String symbol);
}
