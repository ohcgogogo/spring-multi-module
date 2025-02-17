package com.example.appapi.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

// https://cheese10yun.github.io/spring-guide-exception/
// TODO 앞으로 구현해야함.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
        private String message;
        private int status;
        private List<FieldError> errors;
        private String code;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
