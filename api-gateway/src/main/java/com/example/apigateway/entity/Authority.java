package com.example.apigateway.entity;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum Authority {
    ROLE_USER("ROLE_USER", "ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN", "ROLE_ADMIN");

    private final String name;
    private final String code;

    Authority(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Authority ofName(String name) {
        return Authority.valueOf(name);
    }
}
