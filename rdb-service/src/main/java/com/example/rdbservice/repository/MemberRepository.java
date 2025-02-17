package com.example.rdbservice.repository;


import com.example.rdbservice.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

//public interface MemberRepository extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity>, RevisionRepository<MemberEntity, Long, Long> {
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {
    public Optional<MemberEntity> findById(Long id);
    public Optional<MemberEntity> findByEmail(String email);
    public Boolean existsByEmail(String email);
    public List<MemberEntity> findByIdIn(List<Long> ids);
}


    private Long seq;
    private Integer userGroupSeq;
    private String id;
    private String password;
    private Contact contact;
    private Email email;
    private Gender gender;
    private BirthDate birthdate;
    private boolean isDeleted;

    private Long seq;
    @Column(name="user_group_seq", nullable = false)
    private Integer userGroupSeq;
    @Column(length = 100, nullable = false)
    private String id;
    @Column(length = 40, nullable = false)
    private String password;
    @Embedded
    private ContactEntity contact;
    @Embedded
    private EmailEntity email;
    @Column(nullable = false)
    private Gender gender;
    @Embedded
    private BirthDateEntity birthdate;
    @Column(name="delete_flag", nullable = false)
    private boolean isDeleted;
    @Column(name="delete_user_seq")
    private Long deleteUserSeq;
    @Column(name="delete_time")
    private ZonedDateTime deleteTime;
