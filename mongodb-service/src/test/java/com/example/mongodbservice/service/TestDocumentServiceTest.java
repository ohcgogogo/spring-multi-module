package com.example.mongodbservice.service;

import com.example.mongodbservice.entity.TestDocument;
import com.example.mongodbservice.fixtures.TestDocumentFixtures;
import com.example.mongodbservice.repository.TestDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class) // SpringContainer를 로드하지않고 테스트를 위한 기능 사용
class TestDocumentServiceTest {
    @Mock // 모방,가짜(Mock) 객체 사용
    private TestDocumentRepository testDocumentRepository;

    @InjectMocks // 해당 클래스가 필요한 의존성과 맞는 Mock 객체들을 감지하여 해당 클래스의 객체가 만들어질때 사용하여 객체를 만들고 해당 변수에 객체를 주입하
    private TestDocumentService testDocumentService;

    @Test
    void save() {
        TestDocument testDocument = TestDocumentFixtures.createTestDocument("test@email.com");
        given(testDocumentRepository.save(any())).willReturn(testDocument);
        TestDocument savedTestDocument = testDocumentService.save(testDocument);
        assertTrue(savedTestDocument.getEmail().equals("test@email.com"));
    }

    @Test
    void findByEmail() {
        TestDocument testDocument = TestDocumentFixtures.createTestDocument("test@email.com");
        given(testDocumentRepository.findTestDocumentByEmail(any())).willReturn(Optional.ofNullable(testDocument));
        TestDocument findTestDocument = testDocumentService.findByEmail("test@email.com");
        assertTrue(findTestDocument.getEmail().equals("test@email.com"));
    }
}

