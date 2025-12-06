package com.market.quotesservice.controller;

import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.service.QuotesService;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/quotes")
public class QuotesController {

    private final QuotesService quotesService;

    public QuotesController(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    @GetMapping("/{symbol}")
    public QuoteDto getQuote(@PathVariable("symbol") String symbol) {
        return quotesService.getLiveQuote(symbol);
    }
}
