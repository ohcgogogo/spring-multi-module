package com.example.businessservice.event.handler;

import com.example.businessservice.event.SignedupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionalSignupEventHandler {
    /*
        TransactionalEventListener : 트랜잭션과 연관되어 Listerner실행 시점을 제어 가능,
            Listerner에서 오류가 발생한다고 해서 핵심로직의 트랜잭션이 같이 Rollback되지는 않음.

        AFTER_COMMIT (기본값) - 트랜잭션이 성공적으로 마무리(commit)됬을 때 이벤트 실행
        AFTER_ROLLBACK – 트랜잭션이 rollback 됬을 때 이벤트 실행
        AFTER_COMPLETION – 트랜잭션이 마무리 됬을 때(commit or rollback) 이벤트 실행
        BEFORE_COMMIT - 트랜잭션의 커밋 전에 이벤트 실행

        트랜잭션이 커밋된 이후 리스너가 동작한다고 해도, 여전히 하나의 트랜잭션에 묶여있음에 주의
        이미 커밋된 경우 Listener에서 다시 커밋을 할수 없음.
            @TransactionalEventListener의 phase를 BEFORE_COMMIT으로 설정
                Listerer가 여러개인경우 BEFORE_COMMIT은 한개만 작성 가능 (중복 Commit불가)
            혹은 @Transactional의 propagation을 REQUIRES_NEW로 설정
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void insertServerSideNotice(SignedupEvent event) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), event.getMember().toString());
    }
}
