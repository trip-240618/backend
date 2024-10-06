package com.ll.trip.domain.notification.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.notification.notice.dto.NoticeCreateDto;
import com.ll.trip.domain.notification.notice.dto.NoticeDetailDto;
import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/notice/create")
	@Operation(summary = "공지 생성")
	@ApiResponse(responseCode = "200", description = "공지 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = NoticeDetailDto.class))})
	public ResponseEntity<?> createNotice(
		@RequestBody NoticeCreateDto noticeCreateDto
	) {
		NoticeDetailDto response = noticeService.createNotice(noticeCreateDto);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/notice/list")
	@Operation(summary = "공지 목록")
	@ApiResponse(responseCode = "200", description = "공지 목록 보기(파라미터가 없으면 전체 공지 반환)", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NoticeDetailDto.class)))})
	public ResponseEntity<?> showNoticeList(
		@RequestParam(required = false) @Parameter(description = "일반, 업데이트, 시스템", example = "업데이트") String type
	) {
		List<NoticeListDto> response = noticeService.showNoticeList(type);
		return ResponseEntity.ok(response);
	}
}
