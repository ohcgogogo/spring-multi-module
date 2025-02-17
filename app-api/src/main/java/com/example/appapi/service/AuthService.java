package com.example.appapi.service;

import com.example.appapi.config.security.jwt.JwtToken;
import com.example.appapi.config.security.jwt.JwtTokenProvider;
import com.example.appapi.dto.MemberLoginDto;
import com.example.appapi.mapper.MemberControllerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken login(MemberLoginDto memberLoginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = MemberControllerMapper.INSTANCE.convert(memberLoginDto);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), authentication.getName());
        JwtToken jwtToken = jwtTokenProvider.generateTokenDto(authentication);
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), jwtToken.toString());
        // TODO refresh Token을 redis에 저장해야함.
        return jwtToken;
    }
}
