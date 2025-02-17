package com.example.kafkaservice.config;


import com.example.kafkaservice.handler.KafkaManualAcknowledgingErrorHandler;
import com.example.kafkaservice.listener.KafkaConsumerAwareRebalanceListener;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig<K, V> {
    private final ConsumerFactory<K, V> consumerFactory;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaListenerContainerFactoryConsumerConfig()));
        return factory;
    }

    private Map<String, Object> kafkaListenerContainerFactoryConsumerConfig() {
        Map<String, Object> consumerConfig = consumerConfig();
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return consumerConfig;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<K, V> kafkaRebalanceListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaRebalanceListenerContainerFactoryConsumerConfig()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties();
        factory.getContainerProperties().setConsumerRebalanceListener(new KafkaConsumerAwareRebalanceListener());
        /*
            리벨런싱 이슈가 발생하는 경우
            max.poll.records를 줄인다
            max.poll.interval.ms( 디폴트 5분 )를 증가시키기
            session.timeout.ms( 디폴트 10초 )를 증가시키기
         */
        return factory;
    }

    private Map<String, Object> kafkaRebalanceListenerContainerFactoryConsumerConfig() {
        Map<String, Object> consumerConfig = consumerConfig();
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerConfig.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
        return consumerConfig;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<K, V> kafkaBatchAcknowledgingAwareListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaBatchAcknowledgingAwareListenerContainerFactoryConsumerConfig()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(true);
        return factory;
    }

    private Map<String, Object> kafkaBatchAcknowledgingAwareListenerContainerFactoryConsumerConfig() {
        Map<String, Object> consumerConfig = consumerConfig();
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerConfig.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
        return consumerConfig;
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return consumerConfig;
    }

    @Bean
    public KafkaManualAcknowledgingErrorHandler kafkaManualAcknowledgingErrorHandler() {
        return new KafkaManualAcknowledgingErrorHandler();
    }
}
