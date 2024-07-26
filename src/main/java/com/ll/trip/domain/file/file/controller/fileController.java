package com.ll.trip.domain.file.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.dto.PreSignedUrlDto;
import com.ll.trip.domain.file.file.dto.PreSignedUrlRequestBody;
import com.ll.trip.domain.file.file.service.AwsAuthService;

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

	@GetMapping("/request/url")
	public ResponseEntity<PreSignedUrlDto> getPreSignedUrl(
		@RequestBody PreSignedUrlRequestBody requestBody
	) {
		PreSignedUrlDto preSignedUrl = awsAuthService.getPreSignedUrl(requestBody);

		return ResponseEntity.ok(preSignedUrl);
	}



}
