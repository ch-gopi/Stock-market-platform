package com.market.marketsearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandleDto {
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}
