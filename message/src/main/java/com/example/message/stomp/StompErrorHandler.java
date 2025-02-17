package com.example.message.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage,
            Throwable ex) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), clientMessage);
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.getMessage());
        // 오류 메시지가 "UNAUTHORIZED"인 경우 - throw new MessageDeliveryException("UNAUTHORIZED")
//        if ("UNAUTHORIZED".equals(ex.getMessage())) {
//            return errorMessage("유효하지 않은 권한입니다.");
//        }
//        return errorMessage(ex.getMessage());
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> errorMessage(String errorMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
}



