package com.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendMessage {
    private Long id;
    private String message;
}
