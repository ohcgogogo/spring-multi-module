package com.example.entity;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class Channel {
    // TODO 사용자 id별로 채널 명을 관리한다.
    private Map<Long, List<String>> channels;
    @PostConstruct
    private void init() {
        channels = new ConcurrentHashMap<>();
    }
}
