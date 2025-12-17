package com.market.historicalservice.repository;

import com.market.historicalservice.dto.CandleDto;
import com.market.historicalservice.entity.CandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public interface CandleRepository extends JpaRepository<CandleEntity, Long> {



    @Query("SELECT new com.market.historicalservice.dto.CandleDto(c.symbol, c.timestamp, c.open, c.high, c.low, c.close, c.volume) " +
            "FROM CandleEntity c " +
            "WHERE c.symbol = :symbol AND c.timestamp BETWEEN :startTs AND :endTs " +
            "ORDER BY c.timestamp ASC")
    List<CandleDto> findBySymbolAndTimestampBetween(@Param("symbol") String symbol,
                                                    @Param("startTs") long startTs,
                                                    @Param("endTs") long endTs);

}


/*

@Repository
public class CandleRepository {
    private final Map<String, List<CandleDto>> store = new ConcurrentHashMap<>();

    public void save(String symbol, CandleDto candle) {
        store.computeIfAbsent(symbol, k -> new ArrayList<>()).add(candle);
    }
    public List<CandleDto> findBySymbolAndRange(String symbol, String range) {
        List<CandleDto> all = store.getOrDefault(symbol, List.of());
        long cutoff = resolveCutoff(range);

        return all.stream()
                .filter(c -> c.getTimestamp() >= cutoff)
                .toList();
    }
    private long resolveCutoff(String range) {
        long now = System.currentTimeMillis();
        return switch (range) {
            case "1d" -> now - Duration.ofDays(1).toMillis();
            case "5d" -> now - Duration.ofDays(5).toMillis();
            case "1mo" -> now - Duration.ofDays(30).toMillis();
            default -> now - Duration.ofDays(10).toMillis();
        };
    }
    private long alignToMinute(long timestamp) {
        return timestamp - (timestamp % 60000); // floor to minute
    }


}
*/
