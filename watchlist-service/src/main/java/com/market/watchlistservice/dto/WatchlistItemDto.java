package com.market.watchlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WatchlistItemDto {
    private String symbol;
    private double lastPrice;
    private double change;
    private double changePercent;
}
