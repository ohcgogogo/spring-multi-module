package com.example.businessservice.dto;

import com.example.core.entity.Authority;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
@ToString
public class MemberPageableSearchDto {
    String keyword;
    Pageable pageable;
}
