package mss.mindmap.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // Check
        if (isPublicEndpoint(request)) {
            return chain.filter(exchange);
        }

        // Lấy token từ header
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Trả về lỗi 401 Unauthorized
            return this.onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);

        }

        String token = authHeader.substring(7);
        if (!JwtUtils.validateToken(token)) {
            return this.onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        }

        // 3. (Optional) Lấy thông tin user và gắn vào header cho các service phía sau
        String userId = JwtUtils.extractUserId(token);
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", userId)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = String.format("{\"error\": \"%s\"}", errorMessage).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }



    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/v3/api-docs",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/actuator/health",
            "/identity/auth/"

    );

    public static boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::contains);

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
