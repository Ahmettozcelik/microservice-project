package com.microservices.api_gateway.filter;

import com.microservices.api_gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    public GatewayFilter apply(Object config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            if (isPublic(path)) {
                return chain.filter(exchange);
            }

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return unauthorized(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                return unauthorized(exchange);
            }

            Claims claims = jwtUtil.extractClaims(token);

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r.headers(headers -> {
                        headers.add("X-user-id", claims.get("userId").toString());
                        headers.add("X-user-role", claims.get("role").toString());
                        headers.add("X-user-email", claims.getSubject());
                    }))
                    .build();

            return chain.filter(modifiedExchange);
        };
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}