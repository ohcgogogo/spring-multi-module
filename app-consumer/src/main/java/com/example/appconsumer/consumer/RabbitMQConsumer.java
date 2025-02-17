package com.example.appconsumer.consumer;

import com.example.core.entity.Member;
import com.example.rabbitmqservice.event.RabbitMqSignupEventProperties;
import com.example.rabbitmqservice.mapping.Queue01;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {
//    @RabbitListener(queues = "queue-01") // 큐 이름을 직접 지정
//    @RabbitListener(
//        bindings = @QueueBinding(
//                value = @Queue,
//                exchange = @Exchange(value = "exchange-01"),
//                key = "routing_key"
//    )) // exchange와 routing key로 바인딩된 큐에서 메시지를 소비
    @RabbitListener(
        bindings = @QueueBinding(
            exchange = @Exchange(value = RabbitMqSignupEventProperties.EXCHANGE, type = ExchangeTypes.DIRECT),
            value = @Queue(value = RabbitMqSignupEventProperties.QUEUE
                    , arguments = @Argument(name = "x-dead-letter-exchange", value = RabbitMqSignupEventProperties.DEAD_LETTER_EXCHANGE))
        )
        , containerFactory = "rabbitListenerContainerFactory"
        , concurrency = "3"
    )
    public void onMessage(Message message, Channel channel) throws IOException, ClassNotFoundException {
        log.info("{} {} : message received {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        Member member = objectMapper.readValue(message.getBody(), Member.class);
        log.info("{} {} : message received {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), member.toString());

        // 재시도 설정에 따라 재시도 후 여전히 문제가 발생하는 경우 브로커 설정에 따라서 drop되거나 dead-letter exchange로 라우팅 된다.
        // setDefaultRequeueRejected(true)로 설정해야함.
        // setAcknowledgeMode(AcknowledgeMode.AUTO) 로 설정해야함.
//        throw new AmqpRejectAndDontRequeueException("ABCD"); // 실패시 해당 Exception사용하면됨.

//        channel.basicAck(member.getId(), false); // 수동 Ack 전송
        // equeue를 true로 하면 dead letter로 가지 않고 바로 메시지가 있던 원래 queue로 들어감
//        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false); // 수동 NAck 전송
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = RabbitMqSignupEventProperties.DEAD_LETTER_EXCHANGE, type = ExchangeTypes.FANOUT),
                    value = @Queue(value = RabbitMqSignupEventProperties.DEAD_LETTER_QUEUE)
            )
            , ackMode = "MANUAL"
            , containerFactory = "deadLetterContainerFactory"
    )
    public void onDeadLetterMessage(Message message, Channel channel) throws IOException {
        log.info("{} {} : message received {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        Member member = objectMapper.readValue(message.getBody(), Member.class);
        log.info("{} {} : message received {}", Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), member.toString());
        // 여기서 처리 할수 없다고 실패 보고를 한다.
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 수동 Ack 전송
    }
}
