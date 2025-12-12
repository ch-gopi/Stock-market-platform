package com.market.watchlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItemDto {
    private String symbol;
    private double lastPrice;
    private double change;
    private double changePercent;
    private List<Double> sparkline; // closes from historical-service
}
