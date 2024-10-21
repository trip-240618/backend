package com.ll.trip.domain.notification.firebase.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.BatchResponse;
import com.ll.trip.domain.notification.firebase.dto.MessageDto;
import com.ll.trip.domain.notification.firebase.service.FcmMessageUtil;

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

	private final FcmMessageUtil fcmMessageUtil;

	@PostMapping("/fcm/test/send")
	@Operation(summary = "fcm 테스트")
	@ApiResponse(responseCode = "200", description = "알림이 가는지 테스트하는 엔드포인트", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> pushMessage(
		@RequestParam String fcmToken) {
		BatchResponse response = fcmMessageUtil.sendMessage(
				MessageDto.builder()
					.tokenList(List.of(fcmToken))
					.title("test title")
					.body("test content")
					.data(Map.of("destination", "testData"))
					.build());

		return ResponseEntity.ok(response);
	}
}
