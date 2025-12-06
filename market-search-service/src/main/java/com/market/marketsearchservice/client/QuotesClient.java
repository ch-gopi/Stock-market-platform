package com.market.marketsearchservice.client;


import com.market.marketsearchservice.dto.QuoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "quotes-service", url = "${quotes-service.url}")
public interface QuotesClient {
    @GetMapping("/quotes/{symbol}")
    QuoteDto getQuote(@PathVariable("symbol") String symbol);
}
