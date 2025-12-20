package com.market.apigateway.config;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri; // Keycloak realm issuer

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // disable CSRF for APIs
                .authorizeExchange(exchanges -> exchanges
                        // allow preflight requests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // public endpoints
                        .pathMatchers("/auth/register", "/auth/login").permitAll()
                        .pathMatchers("/ws-quotes/**", "/ws-watchlist/**").permitAll()

                        // protected endpoints
                        .pathMatchers("/auth/me").authenticated()
                        .anyExchange().authenticated()
                )
                // ✅ JWT validation via Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder())));

        return http.build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); // frontend origin
        config.addAllowedMethod("*");                     // allow all methods
        config.addAllowedHeader("*");                     // allow all headers
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    /**
     * ✅ Hybrid JWT Decoder:
     * - First tries Keycloak (RSA via JWKS)
     * - Falls back to HMAC secret for legacy tokens
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Keycloak RSA decoder
        ReactiveJwtDecoder keycloakDecoder = ReactiveJwtDecoders.fromIssuerLocation(issuerUri);

        // Legacy HMAC decoder
        SecretKey hmacKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        ReactiveJwtDecoder hmacDecoder = NimbusReactiveJwtDecoder.withSecretKey(hmacKey).build();

        return token -> keycloakDecoder.decode(token)
                .onErrorResume(e -> {
                    log.warn("Keycloak JWT validation failed, trying HMAC decoder: {}", e.getMessage());
                    return hmacDecoder.decode(token);
                });
    }

    /*
    // Optional: inject userId into headers for downstream services
    @Bean
    public GlobalFilter jwtUserInjectionFilter(ReactiveJwtDecoder jwtDecoder) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtDecoder.decode(token)
                        .flatMap(jwt -> {
                            String userId = jwt.getSubject();
                            ServerHttpRequest mutated = exchange.getRequest().mutate()
                                    .header("X-User-Id", userId)
                                    .build();
                            return chain.filter(exchange.mutate().request(mutated).build());
                        })
                        .onErrorResume(e -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
    */
}
