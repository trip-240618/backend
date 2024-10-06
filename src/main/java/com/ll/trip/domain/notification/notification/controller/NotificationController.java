package com.ll.trip.domain.notification.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.flight.dto.ScheduleResponseDto;
import com.ll.trip.domain.notification.firebase.dto.NotificationListDto;
import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 기능")
public class NotificationController {
	private final NotificationService notificationService;

	@Operation(summary = "알림 목록")
	@ApiResponse(responseCode = "200", description = "항공편으로 항공기 출발,도착 정보 조회", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScheduleResponseDto.class))})
	public ResponseEntity<?> showNotification(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam(required = false) @Parameter(description = "알림 종류", example = "여행 일정") String title
	) {
		List<NotificationListDto> response = notificationService.getListByUserIdAndTitle(securityUser.getId(), title);
		return ResponseEntity.ok(response);
	}
}
