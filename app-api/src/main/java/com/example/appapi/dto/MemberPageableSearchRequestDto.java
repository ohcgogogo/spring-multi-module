package com.example.appapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberPageableSearchRequestDto {
    @NotBlank(message = "input keyword empty")
    String keyword;
    @Min(value=1)
    int pageNumber = 1;
    @Min(value=1)
    @Max(value=500)
    int pageSize = 10;
//    public int getPageNumber() { return pageNumber - 1; }
}
