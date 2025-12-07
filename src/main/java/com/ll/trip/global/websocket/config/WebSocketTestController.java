package com.ll.trip.global.websocket.config;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketTestController {
    @MessageMapping("/test/send")
    @SendTo("/topic/api/test")
    public String testSend(String message) {
        return "Server: " + message;
    }
}
