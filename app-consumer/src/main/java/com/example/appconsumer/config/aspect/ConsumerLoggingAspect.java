package com.example.appconsumer.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ConsumerLoggingAspect {
    public static final String CONSUMER_MDC_KEY = "consumer";

    // @KafkaListener 어노테이션이 붙은 메소드 실행 시 아래의 코드가 수행됩니다.
    @Before("(@annotation(org.springframework.kafka.annotation.KafkaListener))")
    public void beforeConsumeKafka(JoinPoint joinPoint) {
        putMDC(joinPoint);
    }
    @Before("(@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener))")
    public void beforeConsumeRabbit(JoinPoint joinPoint) {
        putMDC(joinPoint);
    }

    private void putMDC(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        String consumerInfo =
                joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
        log.info("consumerInfo : {}", consumerInfo);
        MDC.put(CONSUMER_MDC_KEY, consumerInfo);
    }

    // MDC 내 이벤트 컨슈머 진입점 정보가 초기화됩니다.
    private void clearMDC() {
        MDC.clear();
    }

    // @KafkaListener 어노테이션이 붙은 메소드 실행 후 아래의 코드가 수행됩니다.
    @After("(@annotation(org.springframework.kafka.annotation.KafkaListener))")
    public void afterConsumeKafka(JoinPoint joinPoint) {
        clearMDC();
    }

    @After("(@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener))")
    public void afterConsumeRabbit(JoinPoint joinPoint) {
        clearMDC();
    }
}
