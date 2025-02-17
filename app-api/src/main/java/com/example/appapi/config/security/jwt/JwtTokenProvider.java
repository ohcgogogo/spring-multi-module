package com.example.appapi.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// 내부적으로 RuntimeException을 발생시키지 않도록 다시 작성해야 할것으로 보임.
@Slf4j
@Component
public class JwtTokenProvider {
    private final String secretKey;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60L * 540L;
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60L * 60L * 24L * 7L;
    @Lazy
    private Key key;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey
    ) {
        this.secretKey = secretKey;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public JwtToken generateTokenDto(Authentication authentication) {
        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), authentication.toString());
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map( i -> i.getAuthority())
                .collect(Collectors.joining(","));
        Long now = new Date().getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities) // payload "auth": "ROLE_USER"
                .setExpiration(accessTokenExpiresIn) // payload "exp": 1516239022 (예시)
                .signWith(key, SignatureAlgorithm.HS512) // header "alg": "HS512"
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtToken.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) throws MalformedJwtException {
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new MalformedJwtException("권한 정보가 없는 토큰입니다.");
        }

        Collection<GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(i -> new SimpleGrantedAuthority(i))
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Boolean validBlackToken(String token) {
        try {
            // logoutAccessTokenFinder.validBlackToken(token)
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    public Boolean validateToken(String token) throws SecurityException, MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            throw new SecurityException("잘못된 JWT 서명입니다.", e.getCause());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("잘못된 JWT 서명입니다.", e.getCause());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 JWT 토큰입니다.", e.getCause());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다.", e.getCause());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.", e.getCause());
        }
    }

    public Long getExpiration(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getExpiration();
        return expiration.getTime();
    }

    public Long getRemainingTime(String token) {
        return getExpiration(token) - new Date().getTime();
    }

    public Boolean isAlive(String token) {
        return getRemainingTime(token) > 0;
    }

    private Claims parseClaims(String accessToken) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return claims;
    }
}
