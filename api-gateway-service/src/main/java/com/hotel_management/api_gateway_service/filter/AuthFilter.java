package com.hotel_management.api_gateway_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpHeaders headers = exchange.getRequest().getHeaders();

        // Allow auth service without token
        if (path.contains("/auth/")) return chain.filter(exchange);

        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        token = token.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);

            // Route access control
            if (path.startsWith("/api/reports") || path.startsWith("/api/departments")) {
                if (!role.equals("OWNER")) return deny(exchange);
            } else if (path.startsWith("/api/staff") || path.startsWith("/api/inventory") || path.startsWith("/api/rooms")) {
                if (!role.equals("MANAGER")) return deny(exchange);
            } else if (path.startsWith("/api/guests") || path.startsWith("/api/reservations") ||
                    path.startsWith("/api/bills") || path.startsWith("/api/payments")) {
                if (!role.equals("RECEPTIONIST")) return deny(exchange);
            } else {
                return deny(exchange);
            }

            return chain.filter(exchange);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> deny(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
