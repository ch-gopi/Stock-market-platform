package com.market.marketsearchservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class FinnhubSearchResponse {
    private int count;
    private List<FinnhubStockResult> result;
}

