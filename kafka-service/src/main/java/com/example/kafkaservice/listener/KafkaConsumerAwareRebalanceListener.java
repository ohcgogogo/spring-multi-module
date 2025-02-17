package com.example.kafkaservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerAwareRebalanceListener implements ConsumerAwareRebalanceListener {
//    private final Consumer<String, String> consumer;
//    private Map<TopicPartition, OffsetAndMetadata> currentOffSet = new HashMap<>();
//
//    public void addOffset(String topic, int partition, long offset) {
//        currentOffSet.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset));
//    }
//
//    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffSet() {
//        return currentOffSet;
//    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
//        log.info("following partitions are revoked");
//        for (TopicPartition partition : partitions) {
//            log.info(partition.partition() + ", ");
//        }
//        log.info("following partitions are commited");
//
//        for (TopicPartition partition : currentOffSet.keySet()) {
//            log.info(partition.partition() + ", ");
//        }
//
//        consumer.commitSync(currentOffSet);
//        currentOffSet.clear(); // 추가 코드
    }

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
        // consumer.seek(partition, offsetTracker.getOffset() + 1);
        // consumer.seek()을 이용해서 장애 발생 offset부터 읽음.
        for(TopicPartition partition : partitions) {
            log.info(partition.partition() + ", ");
            long nextUncommitedOffset = Long.valueOf(consumer.position(new TopicPartition(partition.topic(), partition.partition())));
            consumer.seek(new TopicPartition(partition.topic(), partition.partition()), nextUncommitedOffset);
        }
    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
        onPartitionsRevoked(partitions);
    }
//
//    @Override
//    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
//        // acknowledge any pending Acknowledgments (if using manual acks)
//    }
//
//    @Override
//    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//        log.info("{}", Thread.currentThread().getStackTrace()[1].getMethodName());
//        store(consumer.position(partition));
//    }
}
