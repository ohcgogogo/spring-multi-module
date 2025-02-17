package com.example.mongodbservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "test")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestDocument {
    @Id
    private Long id;
    private String email;
}
