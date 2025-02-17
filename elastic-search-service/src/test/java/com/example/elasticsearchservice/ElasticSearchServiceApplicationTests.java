package com.example.elasticsearchservice;

import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import com.example.elasticsearchservice.entity.MemberDocument;
import com.example.elasticsearchservice.repository.MemberDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class ElasticSearchServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
