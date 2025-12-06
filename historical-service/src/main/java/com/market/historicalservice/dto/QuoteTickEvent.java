package com.market.historicalservice.dto;


import lombok.Data;

@Data
public class QuoteTickEvent {
    private String symbol;
    private double price;
    private double change;
    private double changePercent;
    private long volume;
    private long timestamp;
}
