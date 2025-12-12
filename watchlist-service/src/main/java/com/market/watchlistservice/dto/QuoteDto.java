package com.market.watchlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDto {
    private String symbol;
    private double price;
    private double change;
    private double changePercent;
    private double volume;
}
