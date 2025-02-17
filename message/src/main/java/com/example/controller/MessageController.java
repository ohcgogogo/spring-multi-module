package com.example.controller;

import com.example.entity.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations sendingOperations;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/send")
    @SendTo("/sub")
    public SendMessage send(SendMessage sendMessage) throws Exception {
//        Thread.sleep(1000); // simulated delay
        return sendMessage;
    }

    @MessageMapping("/sendTo")
//    @SendTo("/sub")
    public void sendTo(SendMessage sendMessage, SimpMessageHeaderAccessor accessor) throws Exception {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), sendMessage.toString());
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), accessor.getUser().toString());
//        Thread.sleep(1000); // simulated delay
        sendingOperations.convertAndSend("/sub/"+sendMessage.getId(), sendMessage);
    }

    @MessageMapping("/sendToUser")
    public void sendToUser(SendMessage sendMessage, SimpMessageHeaderAccessor accessor) throws Exception {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), sendMessage.toString());
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), accessor.getUser().toString());
        // 구독중인 채널이 여러개인경우 모든 채널로 메시지를 보낸다.
        simpMessagingTemplate.convertAndSendToUser(accessor.getUser().getName(), "/queue/message", sendMessage);
    }

//    @MessageMapping("/test/{id}")
//    public String handle(Message message, MessageHeaders messageHeaders,
//                         MessageHeaderAccessor messageHeaderAccessor, SimpMessageHeaderAccessor simpMessageHeaderAccessor,
//                         StompHeaderAccessor stompHeaderAccessor, @Payload String payload,
//                         @Header("destination") String destination, @Headers Map<String, String> headers,
//                         @DestinationVariable String id) {
//
//        System.out.println("---- Message ----");
//        System.out.println(message);
//
//        System.out.println("---- MessageHeaders ----");
//        System.out.println(messageHeaders);
//
//        System.out.println("---- MessageHeaderAccessor ----");
//        System.out.println(messageHeaderAccessor);
//
//        System.out.println("---- SimpMessageHeaderAccessor ----");
//        System.out.println(simpMessageHeaderAccessor);
//
//        System.out.println("---- StompHeaderAccessor ----");
//        System.out.println(stompHeaderAccessor);
//
//        System.out.println("---- @Payload ----");
//        System.out.println(payload);
//
//        System.out.println("---- @Header(\"destination\") ----");
//        System.out.println(destination);
//
//        System.out.println("----  @Headers ----");
//        System.out.println(headers);
//
//        System.out.println("----  @DestinationVariable ----");
//        System.out.println(id);
//
//        return payload;
//    }
}
