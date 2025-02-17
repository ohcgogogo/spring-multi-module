package com.example.businessservice.event;

import com.example.core.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignedupEvent
{
    private Member member;
}
