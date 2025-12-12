package com.market.marketsearchservice.service;
import com.market.marketsearchservice.dto.FinnhubSearchResponse;
import com.market.marketsearchservice.dto.StockSearchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
@Service
public class FinMarketSearchService {

    private final WebClient webClient;
    private final String apiKey;

    public FinMarketSearchService(WebClient.Builder builder,
                                  @Value("${finnhub.api.key}") String apiKey) {
        this.webClient = builder.baseUrl("https://finnhub.io/api/v1").build();
        this.apiKey = apiKey;
    }

    public List<StockSearchDto> searchStocks(String query) {
        FinnhubSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubSearchResponse.class)
                .block();

        return response.getResult().stream()
                .map(r -> new StockSearchDto(
                        r.getSymbol(),
                        r.getDescription(),
                        r.getType(),
                        r.getRegion(),
                        r.getMarketOpen(),
                        r.getMarketClose(),
                        r.getTimezone(),
                        r.getCurrency(),
                        r.getMatchScore()
                ))
                .toList();
    }
}
