package com.example.elasticsearchservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
@ToString
public class MemberDocumentPageableSearchDto {
    String keyword;
    Pageable pageable;
}
