package com.market.quotesservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuoteTickEvent {
    private String symbol;
    private double price;
    private double change;
    private double changePercent;
    private long volume;
    private long timestamp;
}
