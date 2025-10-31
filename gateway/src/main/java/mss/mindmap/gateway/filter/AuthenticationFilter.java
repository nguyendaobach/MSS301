package mss.mindmap.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mss.mindmap.gateway.config.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @Value("${app.api-prefix}")
     String API_PREFIX;

    final ObjectMapper objectMapper;
    final PublicUrlMatcher publicUrlMatcher;
    final RoleAccessMatcher roleAccessMatcher;
    final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath().replaceAll(API_PREFIX, "");
        HttpMethod method = exchange.getRequest().getMethod();

        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

        requestBuilder.header("X-Gateway-Secret", "4391D97158194");

        if (publicUrlMatcher.matches(path, method)) {

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(requestBuilder.build())
                    .build();
            return chain.filter(mutatedExchange);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            return unauthenticated(exchange.getResponse(), "Missing authorization header");
        }

        String token = authHeader.get(0).replace("Bearer ", "");

        if (!jwtUtils.validateToken(token)) {
            return unauthenticated(exchange.getResponse(), "Invalid or expired token");
        }

        List<String> roles = jwtUtils.extractRoles(token);
        if (!roleAccessMatcher.isAuthorized(path, roles, method)) {
            return forbidden(exchange.getResponse(), "Access denied for " + method + " " + path);
        }

        String userId = jwtUtils.extractUserId(token);
        String email = jwtUtils.extractEmail(token);

        requestBuilder
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-User-Email", email != null ? email : "");

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(requestBuilder.build())
                .build();

        return chain.filter(mutatedExchange);
    }


    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = String.format("{\"error\": \"%s\"}", errorMessage).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
    Mono<Void> unauthenticated(ServerHttpResponse response, String message) {
        ResponseEntity<?> apiResponse = ResponseEntity.status(401).body(message);


        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private Mono<Void> forbidden(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String body = String.format("{\"error\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8))));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
