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
