package org.example.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.entity.ResponseData;
import org.example.gateway.feign.AuthService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(0)
@AllArgsConstructor
public class TokenFilter implements GlobalFilter {
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().startsWith("/auth/log")) {
            return chain.filter(exchange);
        }
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        HttpCookie tokenCookie = cookies.getFirst("token");
        if (tokenCookie != null) {
            String token = tokenCookie.getValue();
            log.info("get token: " + token);
            ResponseData<String> responseData = authService.checkToken(token);
            log.info("check token result: " + responseData);
            if (responseData.getStatus() == ResponseData.Status.SUCCESS) {
                ResponseCookie newToken = ResponseCookie.from("token", token).maxAge(60).path("/").build();
                exchange.getResponse().addCookie(newToken);
                return chain.filter(exchange);
            }
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
