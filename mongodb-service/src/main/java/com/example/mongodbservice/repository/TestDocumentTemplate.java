package com.example.mongodbservice.repository;

import com.example.mongodbservice.entity.TestDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

// https://www.baeldung.com/spring-data-mongodb-tutorial
@Slf4j
@Component
@RequiredArgsConstructor
public class TestDocumentTemplate {
// 아래 주석 처리한 방식으로 사용하면 단위 테스트시에 DataMongoTest를 사용 시 문제가 발생함.
//    private final TestDocumentRepository testDocumentRepository;
//    public TestDocument save(TestDocument testDocument) {
//        return testDocumentRepository.save(testDocument);
//    }
//    public Optional<TestDocument> findTestDocumentByEmail(String email) {
//        return testDocumentRepository.findTestDocumentByEmail(email);
//    }

    private final MongoTemplate mongoTemplate;
    public TestDocument save(TestDocument testDocument) {
        return mongoTemplate.insert(testDocument);
    }
    public Optional<TestDocument> findTestDocumentByEmail(String email) {
        return Optional.ofNullable(mongoTemplate.findOne(
            Query.query(Criteria.where("email").is(email)), TestDocument.class
        ));
    }
}
