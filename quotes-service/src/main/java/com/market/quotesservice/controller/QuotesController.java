package com.market.quotesservice.controller;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.service.QuoteCacheService;
import com.market.quotesservice.service.QuoteService;
import com.market.quotesservice.service.QuotesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes")
public class QuotesController {
    private QuoteService quoteService;


    private final QuoteCacheService cacheService;

    public QuotesController(QuoteCacheService cacheService,QuoteService quoteService) {

        this.quoteService=quoteService;
    this.cacheService = cacheService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<QuoteDto> getLatestQuote(@PathVariable("symbol") String symbol) {



        FinQuoteTickEvent tick = cacheService.getLatestTick(symbol);
        if (tick == null) {
            return ResponseEntity.noContent().build(); // 204 if no data
        }

        Double previousClose = cacheService.getPreviousClose(symbol);
        if (previousClose == null || tick==null) {
           QuoteDto quoteDto= quoteService.getTick(symbol);
           return  ResponseEntity.ok(quoteDto);}


        double change = tick.getPrice() - previousClose;
        double changePercent = (previousClose != 0) ? (change / previousClose) * 100 : 0;

        QuoteDto dto = new QuoteDto(
                tick.getSymbol(),
                tick.getPrice(),
                change,
                changePercent,
                tick.getVolume()
        );

        return ResponseEntity.ok(dto);
    }
}


/*
package com.market.quotesservice.controller;

import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.service.QuoteCacheService;
import com.market.quotesservice.service.QuotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/quotes")
    public class QuotesController {

        private final QuoteCacheService cacheService;

        public QuotesController(QuoteCacheService cacheService) {
            this.cacheService = cacheService;
        }

        @GetMapping("/{symbol}")
        public ResponseEntity<QuoteDto> getLatestQuote(@PathVariable String symbol) {
            FinQuoteTickEvent tick = cacheService.getLatestTick(symbol);
            if (tick == null) {
                return ResponseEntity.noContent().build();
            }

            // Compute change and changePercent (requires previous close or last tick)
            double previousClose = cacheService.getPreviousClose(symbol); // implement this
            double change = tick.getPrice() - previousClose;
            double changePercent = (previousClose != 0) ? (change / previousClose) * 100 : 0;

            QuoteDto dto = new QuoteDto(
                    tick.getSymbol(),
                    tick.getPrice(),
                    change,
                    changePercent,
                    tick.getVolume()
            );

            return ResponseEntity.ok(dto);
        }
    }


*/
