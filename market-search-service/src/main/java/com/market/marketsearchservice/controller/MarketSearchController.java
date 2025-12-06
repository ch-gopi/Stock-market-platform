package com.market.marketsearchservice.controller;


import com.market.marketsearchservice.dto.StockSearchDto;
import com.market.marketsearchservice.service.MarketSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class MarketSearchController {

    private final MarketSearchService searchService;

    public MarketSearchController(MarketSearchService searchService) {
        this.searchService = searchService;
    }
    @GetMapping
    public List<StockSearchDto> search(@RequestParam("query") String query) {
        return searchService.searchStocks(query);
    }


}
