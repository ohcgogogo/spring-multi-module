package com.example.appconsumer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNoticeBatchConsumer <K, V> implements BatchAcknowledgingConsumerAwareMessageListener<K, V> {
    @Override
    @KafkaListener(topics = "BatchNotice"
            , groupId = "NoticeBatchGroup"
            , concurrency = "1"
            , containerFactory = "kafkaBatchAcknowledgingAwareListenerContainerFactory"
            , errorHandler = "kafkaManualAcknowledgingErrorHandler"
            , properties = {
                "max.poll.interval.ms:60000",
                "auto.offset.reset:earliest",
                "max.poll.records:100"
            }
    )
    public void onMessage(List<ConsumerRecord<K, V>> records, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        Map<TopicPartition, OffsetAndMetadata> currentOffSet = new HashMap<>();
        records.forEach(record -> {
            log.info("{} : message received {}", Thread.currentThread().getStackTrace()[1].getMethodName(), record.toString());
//            currentOffSet.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
//            consumer.commitSync(currentOffSet); // 이렇게 처리하면 서비스 재기동 시에 마지막 record를 중복처리하는데 원인 확인 필요
//            currentOffSet.clear();
        });
        acknowledgment.acknowledge();
    }
}
