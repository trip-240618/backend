package com.ll.trip.domain.file.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.dto.PreSignedUrlDto;
import com.ll.trip.domain.file.file.service.AwsAuthService;
import com.ll.trip.domain.plan.room.service.PlanRoomService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File", description = "FileApi")
public class fileController {

	private final AwsAuthService awsAuthService;
	private final PlanRoomService planRoomService;

	@GetMapping("/request/url")
	public ResponseEntity<PreSignedUrlDto> getPreSignedUrl(
		@RequestParam String prefix,
		@RequestParam(required = false) String fileName
	) {
		String preSignedUrl = awsAuthService.getPreSignedUrl(prefix, fileName);

		return ResponseEntity.ok(new PreSignedUrlDto(preSignedUrl));
	}



}
