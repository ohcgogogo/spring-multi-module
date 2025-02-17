package com.example.rdbservice.entity;

import com.example.core.entity.Authority;
import jakarta.persistence.*;
import lombok.*;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member",
    uniqueConstraints = {@UniqueConstraint(name = "ux_member__email", columnNames = {"email"})})
@ToString(exclude = {"password"})
//@Audited
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 200, nullable = false)
//    @Audited(withModifiedFlag = true)
    private String password;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
//    @Audited(targetAuditMode = NOT_AUDITED)
    private Authority authority;
    @Builder
    public MemberEntity(String email, String password, Authority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
}
