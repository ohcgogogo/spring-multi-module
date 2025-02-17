package com.example.appapi.config.security.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
@ToString
public class JwtToken {
    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;
/*
    @Builder
    public JwtToken(String grantType, String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
*/
    public ResponseCookie generateCookie() {
        return ResponseCookie.from("refreshToken", this.refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("lax")
            .path("/auth")
            .build();
    }

    public ResponseCookie generateSignOutCookie() {
        return ResponseCookie.from("refreshToken", "")
            .maxAge(1)
            .build();
    }
}

