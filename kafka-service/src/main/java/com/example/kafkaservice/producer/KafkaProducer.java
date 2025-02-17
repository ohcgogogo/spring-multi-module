package com.example.kafkaservice.producer;

public interface KafkaProducer<T> {
    void send(T Message);
}
