package com.market.watchlistservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "watchlist")
@Builder
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // identify user from front-end / user-service
    private String symbol; // AAPL, TSLA, INFY etc.

    public WatchlistEntry(Long userId, String symbol) {
        this.userId = userId;
        this.symbol = symbol;
    }
}
