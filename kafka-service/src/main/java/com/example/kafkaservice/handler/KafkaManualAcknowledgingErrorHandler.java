package com.example.kafkaservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.ManualAckListenerErrorHandler;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
public class KafkaManualAcknowledgingErrorHandler implements ManualAckListenerErrorHandler {
    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer, Acknowledgment ack) {
        log.info("{} : message received {}", Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
        exception.printStackTrace();
        return null;
    }
}
