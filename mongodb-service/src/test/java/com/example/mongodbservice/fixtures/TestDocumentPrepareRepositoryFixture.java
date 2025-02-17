package com.example.mongodbservice.fixtures;

import com.example.mongodbservice.entity.TestDocument;
import com.example.mongodbservice.repository.TestDocumentRepository;
import com.example.mongodbservice.repository.TestDocumentTemplate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestDocumentPrepareRepositoryFixture {
    public static void prepareSave(TestDocumentRepository testDocumentRepository) {
        TestDocument testDocument = TestDocumentFixtures.createTestDocument("test@email.com");
        testDocumentRepository.save(testDocument);
    }
    public static void prepareSave(TestDocumentTemplate testDocumentTemplate) {
        TestDocument testDocument = TestDocumentFixtures.createTestDocument("test@email.com");
        testDocumentTemplate.save(testDocument);
    }
}

