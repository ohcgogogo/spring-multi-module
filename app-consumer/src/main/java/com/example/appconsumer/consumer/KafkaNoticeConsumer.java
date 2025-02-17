package com.example.appconsumer.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNoticeConsumer<K, V> implements AcknowledgingConsumerAwareMessageListener<K, V> {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @KafkaListener(topics = "Notice"
            , groupId = "NoticeGroup"
            , concurrency = "1"
            , containerFactory = "kafkaRebalanceListenerContainerFactory"
            , errorHandler = "kafkaManualAcknowledgingErrorHandler"
    )
    public void onMessage(ConsumerRecord<K, V> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        log.info("{} : message received {}", Thread.currentThread().getStackTrace()[1].getMethodName(), record.toString());
        /*
        - 동기 커밋보다 빠름
        - 중복이 발생할 수 있음
        → 일시적인 통신 문제로 이전 offset보다 이후 offset이 먼저 커밋 될 때
         */
        consumer.commitAsync(new OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                if (exception != null) {
                    log.info("Commit failed");
                } else {
                    log.info("Commit succeeded");
                }
                if (exception != null) {
                    log.info("Commit failed for offsets {}", offsets, exception);
                }
            }
        });
        // 비동기 수동 커밋
        // acknowledgment.acknowledge();

        // 동기/비동기 커밋을 사용하고 싶다면 컨슈머 인스턴스를 파라미터로 받아서 사용하면 된다.
        // poll 메서드 호출후 commitSync 메서드를 명시적으로 호출하는 방법입니다.
        // consumer.commitSync(); // commitSync 메서드는 poll 메서드로 받은 가장 마지막 레코드를 기준으로 커밋을 합니다. 그래서 poll 메서드로 받은 데이터를 모두 처리하고 호출해야하며 커밋을 완료할때까지 기다립니다.
        // consumer.commitSync(curOffset); // 오프셋을 지정해서 commit가능 (Map<TopicPartition, OffsetAndMetadata> curOffset = new HashMap<>();)
        // consumer.commitAsync(); // 커밋이 완료될때까지 기다리지 않음. OffsetCommitCallback() 함수를 파라미터로 사용 해서 체크가능.
        /*
        consumer.commitAsync(new OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                if (exception != null) {
                    log.info("Commit failed");
                } else {
                    log.info("Commit succeeded");
                }
                if (exception != null) {
                    log.info("Commit failed for offsets {}", offsets, exception);
                }
            }
        });
        */
    }
}
