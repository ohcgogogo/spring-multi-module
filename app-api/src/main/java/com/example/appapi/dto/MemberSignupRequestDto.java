package com.example.appapi.dto;

import com.example.appapi.dto.validation.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

//@Getter
//@Setter
//@AllArgsConstructor
//@ToString(exclude = {"password"})
//@Schema(description = "Member signup request")
public class MemberSignupRequestDto {
//    @ToString(exclude = {"password"})
//    @Schema(description = "Member signup request")
    public record Signup(
        @JsonProperty
        @NotBlank(message = "input email empty", groups = ValidationGroups.NotEmptyGroup.class)
        @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]+$", message = "email 형식이 맞지 않습니다.", groups = ValidationGroups.PatternCheckGroup.class)
        @Size(message = "email은 8글자 이상, 20글자 이하입니다.", min = 8, max = 100)
//        @Schema(description = "Email", example = "ben@test.com")
        String email,

        @JsonProperty
        @NotBlank(message = "input password empty", groups = ValidationGroups.NotEmptyGroup.class)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@!%*#?&])[A-Za-z\\d@!%*#?&]{12,}$", message = "password 형식이 맞지 않습니다.", groups = ValidationGroups.PatternCheckGroup.class)
//        @Schema(description = "Password", example = "P!1assword12")
        String password
    ) {
    }

//    @JsonProperty
//    @NotBlank(message = "input email empty", groups = ValidationGroups.NotEmptyGroup.class)
//    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]+$", message = "email 형식이 맞지 않습니다.", groups = ValidationGroups.PatternCheckGroup.class)
//    @Size(message = "email은 8글자 이상, 20글자 이하입니다.", min = 8, max = 100)
//    @Schema(description = "Email", example = "ben@test.com")
//    String email;
//
//    @JsonProperty
//    @NotBlank(message = "input password empty", groups = ValidationGroups.NotEmptyGroup.class)
//    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@!%*#?&])[A-Za-z\\d@!%*#?&]{12,}$", message = "password 형식이 맞지 않습니다.", groups = ValidationGroups.PatternCheckGroup.class)
//    @Schema(description = "Password", example = "P!1assword12")
//    String password;
}