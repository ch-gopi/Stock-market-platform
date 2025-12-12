package com.market.watchlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandleDto {
    private String symbol;
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
}
