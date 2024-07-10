package com.ll.trip.domain.chat.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final SimpMessagingTemplate messagingTemplate;
	@PostMapping("/chat/write")
	@Operation(summary = "메세지 전송")
	public ResponseEntity<?> write(
		@RequestBody final String content) {
		messagingTemplate.convertAndSend("/topic/chat", content);
		return ResponseEntity.ok("{\"message\":\"전송 성공\"}");
	}
}
