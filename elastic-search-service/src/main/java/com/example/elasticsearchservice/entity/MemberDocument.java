package com.example.elasticsearchservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "member_index_v1.0.0")
@TypeAlias("member_index")
@Setter
@Getter
public class MemberDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Text)
    private String email;

    public static String getEmailDefaultFieldName() { return "email"; }
    public static List<String> getEmailFieldNames() { return List.of("email","email.nori"); }
}
