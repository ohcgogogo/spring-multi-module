package com.example.appapi.controller;

import com.example.appapi.dto.ServerSideEventSendRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/serverSideEvent")
@RequiredArgsConstructor
public class ServerSideEventRestController {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /*
        http://localhost:8080/serverSideEvent/subscribe?id=1
        curl -N -m0 --ignore-content-length -X GET "http://localhost:8080/serverSideEvent/subscribe?id=1" -H 'Content-Type:text/event-stream' -H 'Authorization: Bearer '
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam(value="id") Long id) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id);
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        emitters.put(id, emitter);
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        try {
            emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data("EventStream Created. [userId=" + id + "]"));
        } catch (IOException exception) {
            emitters.remove(id);
            emitter.completeWithError(exception);
        }
        return emitter;
    }

    /*
        curl -X POST "http://localhost:8080/serverSideEvent/send?id=1" -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{ "id":"1"}'
     */
    @PostMapping("/send")
    public void send(@RequestBody @Valid ServerSideEventSendRequestDto serverSideEventSendRequestDto) {
        Long id = serverSideEventSendRequestDto.getId();
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id);
        SseEmitter emitter = emitters.get(id);
        try {
            emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data("EventStream Notification. [userId=" + id + "]"));
        } catch (IOException exception) {
            emitters.remove(id);
            emitter.completeWithError(exception);
        }
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "End..");
    }
}

