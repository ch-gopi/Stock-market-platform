package com.market.watchlistservice.repository;

import com.market.watchlistservice.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {



    List<WatchlistEntry> findByUserId(Long userId);
    List<WatchlistEntry> findBySymbol(String symbol);
}
