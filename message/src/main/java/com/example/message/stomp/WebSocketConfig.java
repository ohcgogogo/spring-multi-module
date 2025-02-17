package com.example.message.stomp;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 https://growth-coder.tistory.com/m/157

 "/topic"으로 시작하는 경로는 메시지 브로커를 향하도록 설정한다.
 "/app"으로 시작하는 경로는 @MessageMapping을 향하도록 설정한다.
 구독자는 "/topic/greetings"으로 시작하는 경로를 구독한다. (1번 설정 때문에)
 발행자는 "/app/hello"로 시작하는 경로로 메시지를 보낸다.
 2번 설정 때문에 @MessageMapping("/hello")가 붙어있는 곳으로 메시지가 간다.
 메시지 가공이 끝난 후 ("/topic/greetings")로 보낸다.
 1번 설정 때문에 이 메시지는 메시지 브로커로 가게 된다.
 메시지 브로커에서 "/topic/greetings"를 구독 중인 구독자들에게 메시지를 전송한다.
 */
@Configuration
@EnableWebSocketMessageBroker //웹 소켓 메시지를 다룰 수 있게 허용
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    private final StompErrorHandler stompErrorHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub", "/topic", "/queue"); //발행자가 "/topic"의 경로로 메시지를 주면 구독자들에게 전달
        registry.setApplicationDestinationPrefixes("/pub"); // 발행자가 "/app"의 경로로 메시지를 주면 가공을 해서 구독자들에게 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/stomp/message").setAllowedOriginPatterns("http://localhost:8080").withSockJS(); // 커넥션을 맺는 경로 설정. 만약 WebSocket을 사용할 수 없는 브라우저라면 다른 방식을 사용하도록 설정
//        registry.addEndpoint("/stomp/message").setAllowedOrigins("http://localhost:8080").withSockJS();
        registry.addEndpoint("/stomp/message").setAllowedOriginPatterns("*").withSockJS();
        registry.setErrorHandler(stompErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
