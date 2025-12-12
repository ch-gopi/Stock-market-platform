package com.market.apigateway.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http       .cors(cors -> cors.configurationSource(exchange -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/register", "/auth/login").permitAll()
                        .pathMatchers("/ws-quotes/**").permitAll()   //  allow handshake
                        .pathMatchers("/auth/me").authenticated()
                        .anyExchange().authenticated()
                )



                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${jwt.secret}") String secretKey) {
        ReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(
                new SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA256")
        ).build();

        return token -> decoder.decode(token)
                .doOnNext(jwt -> {
                    log.info("JWT subject: {}", jwt.getSubject());
                    log.info("JWT exp: {}", jwt.getExpiresAt());
                });
    }
}
