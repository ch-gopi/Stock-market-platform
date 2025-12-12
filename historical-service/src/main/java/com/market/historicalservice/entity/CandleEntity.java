package com.market.historicalservice.entity;


import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candles")
public class CandleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
}
