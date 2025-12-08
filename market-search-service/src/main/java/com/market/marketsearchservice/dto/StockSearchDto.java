package com.market.marketsearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSearchDto {
    private String symbol;
    private String name;
    private String type;
    private String region;
    private String marketOpen;
    private String marketClose;
    private String timezone;
    private String currency;
    private double matchScore;

}
