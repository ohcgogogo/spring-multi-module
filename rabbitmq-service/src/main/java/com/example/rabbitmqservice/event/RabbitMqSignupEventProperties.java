package com.example.rabbitmqservice.event;

// 이벤트 별로 Queue를 할당해야 하니까 Event명으로 설정 정보를 넣는다.ss
public interface RabbitMqSignupEventProperties {
    String QUEUE = "queue-01";
    String EXCHANGE = "exchange-01";
    String ROUTING = "routing_key";
    String DEAD_LETTER_QUEUE = "dead-letter-queue-01";
    String DEAD_LETTER_EXCHANGE = "dead-letter-exchange-01";
}
