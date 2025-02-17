package com.example.appconsumer.consumer;

public interface KafkaConsumer<T> {
    void receive(T Message);
}
