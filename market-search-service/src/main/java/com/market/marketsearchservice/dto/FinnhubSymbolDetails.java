package com.market.marketsearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FinnhubSymbolDetails {
    private String description;
    private String displaySymbol;
    private String symbol;
    private String type;
    private String currency;
    private String mic;
    private String figi;
    private String isin;
    private String shareClassFIGI;
    private String marketOpen;
    private String marketClose;
    private String timezone;
}