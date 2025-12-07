package com.market.userservice.controller;

import com.market.userservice.dto.AuthRequest;
import com.market.userservice.service.JwtService;
import com.market.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(Map.of("username", user.getUsername(), "token", token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

    }
    @GetMapping("/me")

    public Mono<Map<String, Object>> me(Authentication authentication) {
        if (authentication == null) {
            return Mono.just(Map.of("error", "Not authenticated"));
        }

        Map<String, Object> userInfo = Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );

        return Mono.just(userInfo);
    }


}
