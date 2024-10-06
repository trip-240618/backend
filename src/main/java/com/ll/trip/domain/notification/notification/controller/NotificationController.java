package com.ll.trip.domain.notification.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.notification.notification.dto.NotificationListDto;
import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "Notification", description = "알림 기능")
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping("/list")
	@Operation(summary = "알림 목록")
	@ApiResponse(responseCode = "200", description = "알림 목록 (파라미터가 없으면 전체 알림 반환)", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> showNotification(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam(required = false) @Parameter(description = "알림 종류 (여행 일정, 여행 기록, 트립스토리)", example = "여행 일정") String title
	) {
		List<NotificationListDto> response = notificationService.getListByUserIdAndTitle(securityUser.getId(), title);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/read")
	@Operation(summary = "단일 읽음 처리")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "실패")
	})
	public ResponseEntity<?> changeIsRead(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @Parameter(description = "읽음처리할 알림의 id", example = "1") long notificationId
	) {
		notificationService.updateIsReadByIdAndUserId(notificationId, securityUser.getId());
		return ResponseEntity.ok("ok");
	}

	@PutMapping("/read/all")
	@Operation(summary = "알림 목록")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(responseCode = "404", description = "실패")
	})
	public ResponseEntity<?> changeAllIsRead(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		notificationService.updateAllIsReadByUserId(securityUser.getId());
		return ResponseEntity.ok("ok");
	}

}
