package com.example.appapi.dto;

import com.example.appapi.dto.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.Set;

@Slf4j
class MemberSignupRequestDtoTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    private String correctPassword = "P1!assword12";

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @Nested
    @DisplayName("email 검증")
    class EmailTest {
        @Test
        @DisplayName("email null 값이면 에러 발생")
        public void email_notnull_validation_fail() {
            //given
            MemberSignupRequestDto.Signup memberSignupRequestDto = new MemberSignupRequestDto.Signup(null, correctPassword);
            //when
            Set<ConstraintViolation<MemberSignupRequestDto.Signup>> violations = validator.validate(memberSignupRequestDto,
                    ValidationGroups.NotEmptyGroup.class);
            //then
            Assertions.assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("email 빈 값이면 에러 발생")
        public void email_notblank_validation_fail() {
            //given
            MemberSignupRequestDto.Signup memberSignupRequestDto = new MemberSignupRequestDto.Signup("", correctPassword);
            //when
            Set<ConstraintViolation<MemberSignupRequestDto.Signup>> violations = validator.validate(memberSignupRequestDto,
                    ValidationGroups.NotEmptyGroup.class);
            //then
            Assertions.assertThat(violations).isNotEmpty();
        }

        @DisplayName("email 형식이 맞지 않으면 에러 발생")
        @ParameterizedTest
//        @ValueSource(strings = {"@vvv.com", "aaa.com", "aaa@.com", "ben@bbb.com", "aaa@bbb.", "ben@bbb.co.kr", "aaa@bbb.co.kr.", "aaa@bbb.co@kr"})
        @ValueSource(strings = {"@vvv.com", "aaa.com", "aaa@.com", "aaa@bbb.", "aaa@bbb.co.kr.", "aaa@bbb.co@kr"})
        public void email_pattern_validation_fail(String email) {
            SoftAssertions.assertSoftly(softly -> {
                MemberSignupRequestDto.Signup memberSignupRequestDto = new MemberSignupRequestDto.Signup(email, correctPassword);
                Set<ConstraintViolation<MemberSignupRequestDto.Signup>> violations = validator.validate(memberSignupRequestDto,
                        ValidationGroups.PatternCheckGroup.class);
                softly.assertThat(violations).as("값을 확인해주세요. 현재 값: %s", email).isNotEmpty();
            });
        }

        @Test
        @DisplayName("email 형식이 맞으면 성공")
        public void email_pattern_validation_success() {
            //given
            MemberSignupRequestDto.Signup memberSignupRequestDto = new MemberSignupRequestDto.Signup("ben@test.com", correctPassword);
            //when
            Set<ConstraintViolation<MemberSignupRequestDto.Signup>> violations = validator.validate(memberSignupRequestDto,
                    ValidationGroups.PatternCheckGroup.class);
            //then
            Assertions.assertThat(violations).isEmpty();
        }
    }
}