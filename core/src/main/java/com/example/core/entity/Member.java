package com.example.core.entity;

import lombok.*;

@Getter
@Setter
//@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"password"})
public class Member {
    private Long id;
    private String email;
    private String password;
    private Authority authority;
    @Builder
    public Member(Long id, String email, String password, Authority authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
}
