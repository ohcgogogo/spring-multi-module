package com.example.mongodbservice.service;

import com.example.mongodbservice.entity.TestDocument;
import com.example.mongodbservice.repository.TestDocumentRepository;
import com.example.mongodbservice.repository.TestDocumentTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestDocumentService {
    private final TestDocumentRepository testDocumentRepository;

    public TestDocument save(TestDocument testDocument) {
        return testDocumentRepository.save(testDocument);
    }
    public TestDocument findByEmail(String email) {
        return testDocumentRepository.findTestDocumentByEmail(email).orElse(null);
    }
}
