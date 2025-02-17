package com.example.rdbservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class MutableBaseEntity extends BaseEntity {
    @LastModifiedDate
    @Column(updatable = true)
    private ZonedDateTime updateTime;

    @LastModifiedBy
    @Column(updatable = true)
    private Long updateUserSeq;
}
