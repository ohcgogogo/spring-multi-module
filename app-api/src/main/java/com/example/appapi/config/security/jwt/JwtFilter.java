package com.example.appapi.config.security.jwt;

import com.example.appapi.config.security.jwt.JwtSecurityUtil;
import com.example.appapi.config.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    public static final String BEARER_PREFIX = "Bearer ";


    // ExceptionTranslationFilter : 이 필터는 FilterSecurityInterceptor 나 애플리케이션에서 올라오는 오류를 가로채 처리 AuthenticationException과 AccessDeniedException을 처리
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String jwt = JwtSecurityUtil.resolveToken(request);
        try {
            if (jwt != null
                    && StringUtils.hasText(jwt)
                    && jwtTokenProvider.validBlackToken(jwt)
                    && jwtTokenProvider.validateToken(jwt)
            ) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.info("Authentication Exception Occurs! - {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
