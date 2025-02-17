package com.example.rabbitmqservice.producer;

import com.example.rabbitmqservice.event.RabbitMqSignupEventProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQNoticeProducer<T> {
//    @Value("${rabbitmq.exchange}")
//    private String exchangeName;
//
//    @Value("${rabbitmq.routing}")
//    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Queue로 메시지를 발행
     *
     * @param message 발행할 메시지의 DTO 객체
     */
    public void send(T message) {
        log.info("{} {} : sendMessage {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
        try {
//            ObjectMapper objectMapper = new ObjectMapper(); // TODO 해당 소스 필요한지 확인
//            String objectToJSON = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(RabbitMqSignupEventProperties.EXCHANGE, RabbitMqSignupEventProperties.ROUTING, message, new CorrelationData(UUID.
                    randomUUID().toString()));
//            rabbitTemplate.convertAndSend(RabbitMqEvent.SIGNUPED_EVENT, message);
        } catch(Exception e) {
            log.info("{} : sendMessage {}", Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage());
        }
    }
}
