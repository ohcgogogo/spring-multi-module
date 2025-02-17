package com.example.review.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ReviewController {
    @RequestMapping("/")
    public String getReview() {
        return "review Information";
    }

    /**
     * curl -X POST http://localhost:8090/api/review/setReview -H 'Content-Type:application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjk5MjU1NTgxfQ.cbLbUUcRj4GAtmwXIekjbkZVcem4eLIBlkvMLpjoOStRthJgfkcZIOzd8M-abNR9PXCbl3jxQ0d6AqBYRjMU8w'
     */
    @RequestMapping(value="/setReview", method = RequestMethod.POST)
    public String setReview(@RequestHeader(value=HttpHeaders.AUTHORIZATION) String authorization) {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8090/api/user")
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorization)
                .build();
        Mono<String> response = client.get().uri("/review/add?userId=333").retrieve().bodyToMono(String.class);
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "Response : "+response.block());
        return "write review";
    }
}
