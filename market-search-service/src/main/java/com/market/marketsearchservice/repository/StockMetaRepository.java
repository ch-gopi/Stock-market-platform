package com.market.marketsearchservice.repository;


import com.market.marketsearchservice.entity.StockMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMetaRepository extends JpaRepository<StockMeta, String> {
    List<StockMeta> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(String symbol, String name);

    List<StockMeta> findBySymbolContainingIgnoreCase(String keyword);
}
