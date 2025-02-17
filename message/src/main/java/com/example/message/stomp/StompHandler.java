package com.example.message.stomp;

import com.example.entity.Authority;
import com.example.entity.JwtTokenUser;
import com.example.util.JwtSecurityUtil;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {
    private final JwtSecurityUtil jwtSecurityUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "Stomp Hanler 실행");
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), message);
//        if(List.of(StompCommand.CONNECT, StompCommand.SUBSCRIBE, StompCommand.SEND).contains(accessor.getCommand())) {
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "인증 진행"); // TODO 그냥 인증을 auth서버를 통해서 api로 처리 하면 중복 소스 제거 가능 
            log.info("{} : '{}'", Thread.currentThread().getStackTrace()[1].getMethodName(), accessor.getNativeHeader("Authorization"));
            String token = jwtSecurityUtil.resolveToken(accessor);
            try {
                jwtSecurityUtil.validateToken(token);
            } catch(Exception e) {
                log.info(e.getMessage());
                throw new MessageDeliveryException(e.getMessage());
//                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
            }
            log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "token check");
            JwtTokenUser jwtTokenUser  = jwtSecurityUtil.getJwtTokenUser(token);
            if(!Authority.ROLE_USER.equals(jwtTokenUser.getAuthority())) {
                throw new MessageDeliveryException("invalid role");
            }
            log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "role check");
            Authentication authentication = jwtSecurityUtil.getAuthentication(token);
            accessor.setUser(authentication);
            log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), authentication.toString());
        }
        return message;
    }

    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("유저 접속...");
                // 유저가 Websocket으로 connect()를 한 뒤 호출됨
                break;
            case DISCONNECT:
                log.info("유저 퇴장...");
                // 유저가 Websocket으로 disconnect() 를 한 뒤 호출됨 or 세션이 끊어졌을 때 발생함(페이지 이동~ 브라우저 닫기 등)
                break;
            case SUBSCRIBE:
                log.info("유저 구독...");
                break;
            case UNSUBSCRIBE:
                log.info("유저 구독 취소...");
                break;
            default:
                break;
        }
    }
}
