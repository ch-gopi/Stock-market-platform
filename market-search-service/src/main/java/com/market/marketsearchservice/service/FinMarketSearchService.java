package com.market.marketsearchservice.service;
import com.market.marketsearchservice.dto.FinnhubSearchResponse;
import com.market.marketsearchservice.dto.FinnhubSymbolDetails;
import com.market.marketsearchservice.dto.FinnhubSymbolMeta;
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
        FinnhubSearchResponse searchResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubSearchResponse.class)
                .block();

        if (searchResponse == null || searchResponse.getResult() == null) {
            return List.of();
        }

        return searchResponse.getResult().stream()
                .map(result -> {
                    String exchange = detectExchange(result.getSymbol());
                    FinnhubSymbolDetails details = null;

                    try {
                        List<FinnhubSymbolDetails> symbols = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/stock/symbol")
                                        .queryParam("exchange", exchange)
                                        .queryParam("token", apiKey)
                                        .build())
                                .retrieve()
                                .bodyToFlux(FinnhubSymbolDetails.class)
                                .collectList()
                                .block();

                        if (symbols != null) {
                            details = symbols.stream()
                                    .filter(d -> d.getSymbol().equals(result.getSymbol()))
                                    .findFirst()
                                    .orElse(null);
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching symbol details for " + result.getSymbol() + ": " + e.getMessage());
                    }

                    Double score = result.getMatchScore();

                    return new StockSearchDto(
                            result.getSymbol(),
                            result.getDescription(),
                            result.getType(),
                            details != null ? details.getMic() : null,
                            details != null ? details.getMarketOpen() : null,
                            details != null ? details.getMarketClose() : null,
                            details != null ? details.getTimezone() : null,
                            details != null ? details.getCurrency() : null,
                            score != null ? score : 0.0
                    );
                })
                .toList();
    }
    private String detectExchange(String symbol) {
        if (symbol.endsWith(".TO")) return "TO";   // Toronto
        if (symbol.endsWith(".MX")) return "MX";   // Mexico
        if (symbol.endsWith(".VI")) return "VI";   // Vienna
        if (symbol.endsWith(".WA")) return "WA";   // Warsaw
        if (symbol.endsWith(".NE")) return "NE";   // Canada NEO
        if (symbol.endsWith(".L"))  return "L";    // London
        if (symbol.endsWith(".AS")) return "AS";   // Amsterdam
        if (symbol.endsWith(".RO")) return "RO";   // Bucharest
        if (symbol.endsWith(".SN")) return "SN";   // Santiago
        // default to US if no suffix
        return "US";
    }

}
