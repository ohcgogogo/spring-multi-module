package com.example.appapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"password"})
public class MemberLoginRequestDto {
    @JsonProperty
    @NotBlank(message = "input email empty")
    String email;

    @JsonProperty
    @NotBlank(message = "input password empty")
    String password;
}
