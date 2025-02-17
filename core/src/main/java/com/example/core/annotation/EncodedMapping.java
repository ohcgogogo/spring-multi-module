package com.example.core.annotation;

import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// https://sanghye.tistory.com/39
@Qualifier // org.mapstruct.Qualifier 사용할 의존 객체를 선택할 수 있도록 해준다.
@Target({ElementType.METHOD, ElementType.TYPE}) // 사용자가 만든 어노테이션이 부착될 수 있는 타입
@Retention(RetentionPolicy.CLASS ) // 어노테이션으로 어느 시점까지 어노테이션의 메모리를 가져갈 지 설정
public @interface EncodedMapping {
}
