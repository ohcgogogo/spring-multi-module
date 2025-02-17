package com.example.apigateway.config.jwt;

import com.example.apigateway.entity.Authority;
import com.example.apigateway.entity.JwtTokenUser;
import com.example.apigateway.util.JwtSecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 curl -X GET http://localhost:8090/api/user/ -H 'Content-Type:application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjk5Mzc4NDUzfQ.ofOumK9w7DyIq3b3Qh3gsWTY1FCNTag4UmE6r5put0S2ZCIEVB0Okge1FO2J9aXLCT_oqhOOmNSviPMc-8HxYg'
 */
@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Autowired
    private ObjectMapper objectMapper;
    private static final String ROLE_KEY = "role";

    private final JwtSecurityUtil jwtSecurityUtil;
    private final Environment environment;
    public AuthorizationHeaderFilter(
        Environment environment,
        JwtSecurityUtil jwtSecurityUtil
    ) {
        super(Config.class);
        this.environment = environment;
        this.jwtSecurityUtil = jwtSecurityUtil;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(ROLE_KEY);
    }

    @Setter
    @Getter
    public static class Config {
        private String role;
    }

    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {
        return (exchange, chain) -> {
            log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "인증체크");
            ServerHttpRequest request = exchange.getRequest();

            request.getHeaders().toSingleValueMap().entrySet().stream()
                    .forEach(i -> log.info("{} : {}", i.getKey(), i.getValue()));

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorization header");
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
//                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authorization header"));
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = authorizationHeader.replace(jwtSecurityUtil.BEARER_PREFIX, "");

            // TODO 나중에 Token Validateion과 Parsing은 Auth 서비스에 Api로 요청해서 조치한다.
            try {
                jwtSecurityUtil.validateToken(token);
            } catch(Exception e) {
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            JwtTokenUser jwtTokenUser  = jwtSecurityUtil.getJwtTokenUser(token);

            if(!config.getRole().equals(jwtTokenUser.getAuthority().getName())) {
                return onError(exchange, "invalid role", HttpStatus.UNAUTHORIZED);
            }

            request.mutate()
                    .header("X-Authorization-Id", String.valueOf(jwtTokenUser.getId()))
                    .header("X-Authorization-Role", jwtTokenUser.getAuthority().getName())
                    .build();

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(httpStatus);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                byte[] errorResponse = objectMapper.writeValueAsBytes(err);
                return bufferFactory.wrap(errorResponse);
            } catch (Exception e) {
                log.error("error", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }

//    public Boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(environment.getProperty("jwt.secret")))).build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (SecurityException e) {
//            throw new RuntimeException("잘못된 JWT 서명입니다.");
//        } catch (MalformedJwtException e) {
//            throw new RuntimeException("잘못된 JWT 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            throw new RuntimeException("만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            throw new RuntimeException("지원되지 않는 JWT 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("JWT 토큰이 잘못되었습니다.");
//        }
//    }
}
