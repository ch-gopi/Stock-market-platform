package com.market.watchlistservice.dto;


import lombok.Data;


@Data
public class QuoteTickEvent {
    private String symbol;
    private double open;
    private double high;
    private double low;
    private double price;
    private long volume;
    private String latestTradingDay;
    private double previousClose;
    private double change;
    private String changePercent;

}
