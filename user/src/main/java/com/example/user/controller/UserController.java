package com.example.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class UserController {

    @RequestMapping("/")
    public String getUser(@RequestHeader HttpHeaders headers) {
        headers.toSingleValueMap().entrySet().stream()
                .forEach(i -> log.info("{} : {}", i.getKey(), i.getValue()));
        return "user Information";
    }

    @RequestMapping("/review/add")
    public Mono<ResponseEntity> addReviewCount(@RequestParam(value="userId") int userId) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "add User Review Count / user Id : "+userId);
        return Mono.just(ResponseEntity.ok().build());
    }
}
