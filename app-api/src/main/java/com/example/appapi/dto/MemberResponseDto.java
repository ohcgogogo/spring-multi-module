package com.example.appapi.dto;

import com.example.core.entity.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseDto {
    private Long id;
    private String email;
}
