package com.market.marketsearchservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stock_meta")
@Data
public class StockMeta {
    @Id
    private String symbol;
    private String name;
    private String type;
    private String region;
    private String marketOpen;
    private String marketClose;
    private String timezone;
    private String currency;
    private double matchScore;
    private double price;
    private double change;
    private double changePercent;
    private long volume;
    private double historicalPerformance;
}
