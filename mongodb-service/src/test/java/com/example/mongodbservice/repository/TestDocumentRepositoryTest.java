package com.example.mongodbservice.repository;

import com.example.mongodbservice.PropertiesLocation;
import com.example.mongodbservice.PropertyOverrideContextInitializer;
import com.example.mongodbservice.TestConfiguration;
import com.example.mongodbservice.entity.TestDocument;
import com.example.mongodbservice.fixtures.TestDocumentPrepareRepositoryFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// https://www.baeldung.com/spring-boot-embedded-mongodb
// https://www.baeldung.com/spring-tests-override-properties
@DataMongoTest // 내장 DB를 사용
//@Transactional
//@Rollback
@ExtendWith({SpringExtension.class}) // SpringContainer를 로드
@ContextConfiguration(
    initializers = PropertyOverrideContextInitializer.class,
    classes = TestConfiguration.class
)
@TestPropertySource(properties = {"spring.config.location = "+ PropertiesLocation.VALUE})
@ActiveProfiles({"default","develop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll을 non-static으로 사용하기 위해 설정
//@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
class TestDocumentRepositoryTest {
    @Autowired
    private TestDocumentRepository testDocumentRepository;

    @BeforeAll
    void prepareSave() {
        TestDocumentPrepareRepositoryFixture.prepareSave(testDocumentRepository);
//        TestDocument testDocument = new TestDocument(1L,"test@email.com");
//        testDocumentRepositoryInterface.save(testDocument);
    }

    @Test
    void findTestDocumentByEmail() {
//        this.prepareSave();
        Optional<TestDocument> findTestDocument = testDocumentRepository.findTestDocumentByEmail("test@email.com");
        assertTrue(findTestDocument.get().getEmail().equals("test@email.com"));
    }
}