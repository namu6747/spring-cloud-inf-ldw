package com.example.apigatewayservice.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Environment environment;
    private final JwtParser jwtParser;

    public AuthorizationHeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
        Key secretKey = Keys.hmacShaKeyFor(environment.getProperty("token.secret").getBytes(StandardCharsets.UTF_8));
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (log.isInfoEnabled()) {
                log.warn("environment.getProperty(\"token.secret\") : {}", environment.getProperty("token.secret"));
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = authorizationHeader.replace("Bearer", "");

            if (!isJwtValid(token)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isJwtValid(String token) {
        boolean isJwt = true;
        String subject = null;

        try {
            /**
             * parser.parse(token)은 토큰을 파싱하여 JWS 서명 여부를 확인하고, 서명이 있는 경우에는 Jws<Claims> 형식으로 파싱합니다.
             * 따라서 이 경우에는 서명이 있는 JWT 토큰을 파싱할 수 있습니다.
             *
             * parser.parseClaimsJws(token)는 명시적으로 JWS 서명을 파싱하도록 지정한 것입니다.
             * 이를 사용하면 서명이 있는 JWT 토큰만 파싱할 수 있습니다. 파싱된 결과는 Jws<Claims> 형식으로 반환됩니다.
             *
             * 둘 다 사용 가능한 이유는 JWT 라이브러리가 토큰의 형식을 분석하여 내부적으로 알맞은 처리를 수행하기 때문입니다.
             * 서명이 있는 JWT 토큰을 파싱할 때는 parseClaimsJws()를 사용하는 것이 명시적이고 명확한 방법입니다.
             * 하지만 서명이 없는 JWT 토큰도 parse()를 사용하여 파싱할 수 있습니다.
             */
            Jwt jwt = jwtParser.parse(token);
            log.info("jwtToken.getHeader() : {}", jwt.getHeader());
            log.info("jwtToken.getBody() : {}", jwt.getBody());

            Jws<Claims> jws = jwtParser.parseClaimsJws(token);
            log.info("jws.getHeader() : {}", jws.getHeader());
            log.info("jws.getBody() : {}", jws.getBody());
            log.info("jws.getSignature() : {}", jws.getSignature());

            subject = jws.getBody().getSubject();
            log.info("subject : {}", subject);
        } catch (Exception e) {
            log.error("jwt error : ", e);
            return false;
        }

        if (!StringUtils.hasText(subject)) {
            return false;
        }

        return isJwt;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus statusCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(statusCode);

        log.error(err);
        return response.setComplete();
    }

    @Data
    public static class Config {
        private String secret;
    }

}
