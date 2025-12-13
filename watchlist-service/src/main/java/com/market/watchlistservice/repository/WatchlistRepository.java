package com.market.watchlistservice.repository;

import com.market.watchlistservice.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {


        Optional<WatchlistEntry> findByUserIdAndSymbolIgnoreCase(Long userId, String symbol);



    List<WatchlistEntry> findByUserId(Long userId);
    List<WatchlistEntry> findBySymbol(String symbol);
}
