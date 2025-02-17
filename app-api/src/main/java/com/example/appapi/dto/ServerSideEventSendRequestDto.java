package com.example.appapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerSideEventSendRequestDto {
    @JsonProperty
    Long id = 0L;
}
