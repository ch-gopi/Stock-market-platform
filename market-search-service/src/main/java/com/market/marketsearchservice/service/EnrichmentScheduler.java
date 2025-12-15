package com.market.marketsearchservice.service;



import com.market.marketsearchservice.config.StockEventPublisher;
import com.market.marketsearchservice.repository.StockMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnrichmentScheduler {

    private final StockMetaRepository stockMetaRepository;
    private final StockEventPublisher publisher;

    public EnrichmentScheduler(StockMetaRepository stockMetaRepository,
                               StockEventPublisher publisher) {
        this.stockMetaRepository = stockMetaRepository;
        this.publisher = publisher;
    }

    // Every 5 minutes, publish enrichment events for all symbols
    @Scheduled(fixedDelay = 300_000)
    public void refreshAll() {
        stockMetaRepository.findAll().forEach(meta -> {
            publisher.publishEnrichmentEvent(meta.getSymbol());
        });
        log.info("Published enrichment events for all symbols");
    }
}
