package com.market.marketsearchservice.dto;


import lombok.Data;

@Data
public class FinnhubStockResult {
    private String symbol;
    private String description;
    private String type;
    private String region;
    private String marketOpen;
    private String marketClose;
    private String timezone;
    private String currency;
    private double matchScore;
}
