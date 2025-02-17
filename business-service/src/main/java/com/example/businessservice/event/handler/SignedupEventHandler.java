package com.example.businessservice.event.handler;

import com.example.businessservice.event.SignedupEvent;
import com.example.elasticsearchservice.service.MemberDocumentRepositoryService;
import com.example.kafkaservice.producer.KafkaNoticeProducer;
import com.example.rabbitmqservice.producer.RabbitMQNoticeProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignedupEventHandler {
    private final KafkaNoticeProducer producer;
    private final MemberDocumentRepositoryService memberDocumentRepositoryService;
    private final RabbitMQNoticeProducer rabbitMQNoticeProducer;

    /*
        EventListener publishEvent하는 시점에 동기화되어 바로 Listener가 동작함.
        @Async을 통해 비동기로 실행도 가능

        EventListener를 사용하던지 TransactionalEventListener를 사용하던지 Listener처리가 핵심 로직에 영향을 주지 않는다.
        그렇기 때문에 핵심로직과 함께 반드시 실행되어야 하는 중요한 로직은 Kafka등을 통해서 재처리가 가능하도록 조치해함.
     */
    @Async
    @EventListener
    public void insertServerSideNotice(SignedupEvent event) {
//        Thread.sleep(2000); // 2초
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), event.getMember().toString());
        producer.send(event.getMember().getEmail() + "님 가입을 환영합니다.");
        rabbitMQNoticeProducer.send(event.getMember()); // TODO dto를 주고 받을지 event 자체를 주고 받을지 판단해야함.
//        rabbitMQNoticeProducer.sendMessage(event.getMember().toString());
    }

    @Async
    @EventListener
    public void insertSearchKeyword(SignedupEvent event) {
//        Thread.sleep(2000); // 2초
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), event.getMember().toString());
        memberDocumentRepositoryService.save(event.getMember());
    }
}
