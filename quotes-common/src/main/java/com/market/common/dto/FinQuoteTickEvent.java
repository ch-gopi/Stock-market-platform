package com.market.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinQuoteTickEvent implements Serializable {
    private String symbol;
    private double price;
    private long timestamp;
    private double volume;



    // getters/setters
}
