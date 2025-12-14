package com.market.marketsearchservice.dto;


import lombok.Data;
@Data
public class FinnhubStockResult {
    private String description;
    private String displaySymbol;
    private String symbol;
    private String type;
    private Double matchScore; // optional, only present in some responses
}
