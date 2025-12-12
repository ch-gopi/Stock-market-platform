package com.market.marketsearchservice.controller;
import com.market.marketsearchservice.dto.StockSearchDto;
import com.market.marketsearchservice.service.FinMarketSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/search")
public class FinMarketSearchController {

    private final FinMarketSearchService marketSearchService;

    public FinMarketSearchController(FinMarketSearchService marketSearchService) {
        this.marketSearchService = marketSearchService;
    }

    @GetMapping
    public List<StockSearchDto> search(@RequestParam("query") String query) {
        return marketSearchService.searchStocks(query);
    }
}
