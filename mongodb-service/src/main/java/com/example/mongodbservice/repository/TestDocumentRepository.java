package com.example.mongodbservice.repository;

import com.example.mongodbservice.entity.TestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
public interface TestDocumentRepository extends MongoRepository<TestDocument, String> {
     Optional<TestDocument> findTestDocumentByEmail(String email);
}
