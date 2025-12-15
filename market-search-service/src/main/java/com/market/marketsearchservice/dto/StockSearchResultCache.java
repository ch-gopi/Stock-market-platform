package com.market.marketsearchservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockSearchResultCache {
    private List<StockSearchDto> results;
    // getters/setters
}
