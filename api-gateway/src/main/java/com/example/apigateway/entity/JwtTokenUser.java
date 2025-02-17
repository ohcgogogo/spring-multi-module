package com.example.apigateway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class JwtTokenUser {
    private Long id;
    private Authority authority;
}