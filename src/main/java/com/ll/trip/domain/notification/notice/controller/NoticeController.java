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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/notice/create")
	public ResponseEntity<?> createNotice(
		@RequestBody NoticeCreateDto noticeCreateDto
	) {
		NoticeDetailDto response = noticeService.createNotice(noticeCreateDto);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/notice/list")
	public ResponseEntity<?> showNoticeList(
		@RequestParam(required = false) Boolean normal,
		@RequestParam(required = false) Boolean update,
		@RequestParam(required = false) Boolean system
	) {
		List<NoticeListDto> resposen = noticeService.showNoticeList(normal, update, system);
		return ResponseEntity.ok(resposen);
	}
}
