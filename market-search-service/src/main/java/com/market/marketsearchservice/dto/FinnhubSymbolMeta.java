package com.market.marketsearchservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class FinnhubSymbolMeta {
    private String country;
    private String currency;
    private String exchange;
    private String ipo;
    private Double marketCapitalization;
    private String name;
    private String phone;
    private Double shareOutstanding;
    private String ticker;
    private String weburl;
    private String logo;
    private String finnhubIndustry;


    // If you want marketOpen/marketClose/timezone, use /stock/symbol endpoint instead
}
