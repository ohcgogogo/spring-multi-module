package com.example.elasticsearchservice.repository;

import com.example.elasticsearchservice.TestConfiguration;
import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import com.example.elasticsearchservice.entity.MemberDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {"spring.config.location = classpath:application-elastic-search.yml"})
@ActiveProfiles({"default","develop"})
class MemberDocumentRepositoryTest {
    @Autowired
    MemberDocumentRepository memberDocumentRepository;

    @Test
    void search() {
        MemberDocumentPageableSearchDto memberDocumentPageableSearchDto = MemberDocumentPageableSearchDto.builder()
                .keyword("한글로 가입해 다시 我注册")
                .pageable(PageRequest.of(0, 10))
                .build();
        SearchHits<MemberDocument> memberDocumentSearchHits = memberDocumentRepository.search(memberDocumentPageableSearchDto);
        assertTrue(true);
    }
}