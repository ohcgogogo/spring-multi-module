package com.example.core.mapper;

import com.example.core.annotation.EncodedMapping;
import com.example.core.util.DefaultPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderMapper {
    private final PasswordEncoder passwordEncoder = DefaultPasswordEncoder.getDefaultPasswordEncoder();

    @EncodedMapping
    public String encode(String value) {
        return passwordEncoder.encode(value);
    }
}
