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
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith({SpringExtension.class})
@DataMongoTest
@ContextConfiguration(
        initializers = PropertyOverrideContextInitializer.class,
        classes = TestConfiguration.class
)
@TestPropertySource(properties = {"spring.config.location = "+ PropertiesLocation.VALUE})
@ActiveProfiles({"default","develop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll을 non-static으로 사용하기 위해 설정
class TestDocumentTemplateTest {
    @MockBean
    private TestDocumentRepository testDocumentRepositoryInterface;

    @SpyBean
    private TestDocumentTemplate testDocumentTemplate;

    @BeforeAll
    void prepareSave() {
        TestDocumentPrepareRepositoryFixture.prepareSave(testDocumentTemplate);
//        TestDocument testDocument = new TestDocument(1L,"test@email.com");
//        testDocumentRepositoryInterface.save(testDocument);
    }

    @Test
    void check() {
        assertTrue(true);
    }

    @Test
    void save() {
        TestDocument testDocument = new TestDocument(2L,"test@email.com");
        TestDocument savedTestDocument = testDocumentTemplate.save(testDocument);
        assertTrue(savedTestDocument.getEmail().equals("test@email.com"));
    }

    @Test
    void findTestDocumentByEmail() {
        given(testDocumentTemplate.findTestDocumentByEmail(any())).willReturn(Optional.of(new TestDocument(2L,"test@email.com")));

        Optional<TestDocument> findTestDocument = testDocumentTemplate.findTestDocumentByEmail("test@email.com");
        assertTrue(findTestDocument.isPresent() ? findTestDocument.get().getEmail().equals("test@email.com") : false);
    }
}