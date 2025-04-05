package com.ll.trip.domain.notification.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.notification.notice.dto.NoticeCreateDto;
import com.ll.trip.domain.notification.notice.dto.NoticeDetailDto;
import com.ll.trip.domain.notification.notice.dto.NoticeListDto;
import com.ll.trip.domain.notification.notice.service.NoticeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@Tag(name = "Notice")
public class NoticeController {

	private final NoticeService noticeService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	@Operation(summary = "공지 생성")
	public ResponseEntity<NoticeDetailDto> createNotice(
		@RequestBody NoticeCreateDto noticeCreateDto
	) {
		NoticeDetailDto response = noticeService.createNotice(noticeCreateDto);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/modify/{noticeId}")
	@Operation(summary = "공지 수정")
	public ResponseEntity<NoticeDetailDto> modifyNotice(
		@PathVariable @Parameter(description = "Notice id", example = "1", in = ParameterIn.PATH) long noticeId,
		@RequestBody NoticeCreateDto noticeCreateDto
	) {
		NoticeDetailDto response = noticeService.modifyNotice(noticeId, noticeCreateDto);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/delete/{noticeId}")
	@Operation(summary = "공지 삭제")
	public ResponseEntity<String> deleteNotice(
		@PathVariable @Parameter(description = "Notice id", example = "1", in = ParameterIn.PATH) long noticeId
	) {
		noticeService.deleteNotice(noticeId);
		return ResponseEntity.ok("deleted");
	}

	@GetMapping("/list")
	@Operation(summary = "공지 목록", description = "공지 목록 보기(파라미터가 없으면 전체 공지 반환)")
	public ResponseEntity<List<NoticeListDto>> showNoticeList(
		@RequestParam(required = false) @Parameter(description = "일반, 업데이트, 시스템", example = "업데이트") String type
	) {
		List<NoticeListDto> response = noticeService.showNoticeList(type);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/detail/{noticeId}")
	@Operation(summary = "공지 상세")
	public ResponseEntity<NoticeDetailDto> showNoticeDetail(
		@PathVariable @Parameter(description = "Notice id", example = "1", in = ParameterIn.PATH) long noticeId,
		@RequestParam(required = false) @Parameter(description = "일반, 업데이트, 시스템", example = "업데이트") String type
	) {
		NoticeDetailDto response = noticeService.showNoticeDetail(noticeId);
		return ResponseEntity.ok(response);
	}
}
