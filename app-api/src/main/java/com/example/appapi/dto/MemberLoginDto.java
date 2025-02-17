package com.example.appapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"password"})
public class MemberLoginDto {
    String email;
    String password;
}
