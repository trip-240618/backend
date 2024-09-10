package com.ll.trip.domain.alarm.firebase.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.alarm.firebase.dto.AlarmResponseDto;
import com.ll.trip.domain.alarm.firebase.service.FcmService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FcmController {

	private final FcmService fcmService;

	@PostMapping("/fcm/test/send")
	@Operation(summary = "fcm 테스트")
	@ApiResponse(responseCode = "200", description = "알림이 가는지 테스트하는 엔드포인트", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> pushMessage(@RequestBody @Validated AlarmResponseDto alarmResponseDtoDto) {
		log.debug("[+] 푸시 메시지를 전송합니다. ");
		try {
			int result = fcmService.sendMessageTo(alarmResponseDtoDto);
		}catch (IOException e) {
			return ResponseEntity.internalServerError().body(e);
		}


		return ResponseEntity.ok("Success");
	}
}
