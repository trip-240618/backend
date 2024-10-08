package com.ll.trip.domain.notification.firebase.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ll.trip.domain.notification.firebase.service.FcmService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "FCM Test")
public class FcmController {

	private final FcmService fcmService;

	@PostMapping("/fcm/test/send")
	@Operation(summary = "fcm 테스트")
	@ApiResponse(responseCode = "200", description = "알림이 가는지 테스트하는 엔드포인트", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> pushMessage(
		@RequestParam String fcmToken) {
		log.debug("[+] 푸시 메시지를 전송합니다. ");
		try {
			int result = fcmService.sendMulticastMessage(List.of(fcmToken), "test title", "test content");
			return ResponseEntity.ok("failure count: " + result);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body(e);
		} catch (FirebaseMessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
