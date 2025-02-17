package com.example.mongodbservice.fixtures;

import com.example.mongodbservice.entity.TestDocument;

public class TestDocumentFixtures {
    public static TestDocument createTestDocument(String email) {
        return new TestDocument(1L,email);
    }
}
