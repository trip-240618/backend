package com.ll.trip.domain.file.file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.dto.PreSignedUrlResponseDto;
import com.ll.trip.domain.file.file.dto.PreSignedUrlRequestBody;
import com.ll.trip.domain.file.file.service.AwsAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
	@Operation(summary = "s3 버킷 오브젝트 권한 요청")
	@ApiResponse(responseCode = "200", description = "권한이 부여된 url을 리턴 해당 url에 이미지를 업로드할 수 있음", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = PreSignedUrlResponseDto.class))})
	public ResponseEntity<PreSignedUrlResponseDto> getPreSignedUrl(
		@RequestBody PreSignedUrlRequestBody requestBody
	) {
		PreSignedUrlResponseDto responseDto = awsAuthService.getPreSignedUrl(requestBody);

		return ResponseEntity.ok(responseDto);
	}



}
