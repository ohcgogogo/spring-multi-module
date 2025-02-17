package com.example.apigateway.util;

import com.example.apigateway.entity.Authority;
import com.example.apigateway.entity.JwtTokenUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtSecurityUtil {
    private final String secretKey;
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORITIES_KEY = "auth";

    @Lazy
    private Key key;

    public JwtSecurityUtil(
            @Value("${jwt.secret}") String secretKey
    ) {
        this.secretKey = secretKey;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            throw new RuntimeException("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 토큰이 잘못되었습니다.");
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(i -> new SimpleGrantedAuthority(i))
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public JwtTokenUser getJwtTokenUser(String token) {
        Authentication authentication = getAuthentication(token);
        String authorities = authentication.getAuthorities().stream()
                .map( i -> i.getAuthority())
                .collect(Collectors.joining(","));
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), authorities);
        return JwtTokenUser.builder().id(Long.parseLong(authentication.getName())).authority(Authority.ofName(authorities)).build();
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
