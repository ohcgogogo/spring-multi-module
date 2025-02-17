package com.example.businessservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"password"})
public class MemberSignupDto {
    String email;
    String password;
}