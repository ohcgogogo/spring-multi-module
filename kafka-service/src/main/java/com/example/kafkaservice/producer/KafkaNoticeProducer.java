package com.example.kafkaservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNoticeProducer<T> implements KafkaProducer<T> {
    private static final String TOPIC = "BatchNotice";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(T message) {
        log.info("{} {} : Produce message {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(TOPIC, message.toString());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        completableFuture.whenComplete((sendResult, exception) -> {
            if (sendResult != null){
                log.info("KafkaNoticeProducer success send : {}", sendResult);
            } else if (exception != null){
                log.info("KafkaNoticeProducer fail send : {}", exception.getMessage());
            }
        })
        .runAsync(()-> {
            try {
                log.info("{} : runAsync {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "runAsync");
            } catch(Exception e) {
                throw e;
            }
        }, executor)
        .thenRun(()->log.info("{} : thenRun {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "thenRun"));
    }
}
