package com.ll.trip.domain.file.file.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.file.file.dto.DeleteObjectByUrlRequestBody;
import com.ll.trip.domain.file.file.dto.PreSignedUrlResponseDto;
import com.ll.trip.domain.file.file.service.AwsAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File", description = "FileApi")
public class fileController {

	private final AwsAuthService awsAuthService;

	@GetMapping("/request/url")
	@Operation(summary = "s3 버킷 오브젝트 권한 요청")
	@ApiResponse(responseCode = "200",
		description = "권한이 부여된 url을 리턴 해당 url에 이미지를 업로드할 수 있음, "
					  + "prefix는 파일의 경로(plan 또는 profile 등), photoCnt는 업로드할 파일의 수", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = PreSignedUrlResponseDto.class))})
	public ResponseEntity<PreSignedUrlResponseDto> getPreSignedUrl(
		@RequestParam @Parameter(description = "경로", example = "profile or history ..") String prefix,
		@RequestParam @Parameter(description = "업로드할 사진 개수", example = "1") int photoCnt
	) {
		PreSignedUrlResponseDto responseDto = awsAuthService.getPreSignedUrl(prefix, photoCnt);

		return ResponseEntity.ok(responseDto);
	}

	@PostMapping("/delete/url")
	@Operation(summary = "s3 버킷 오브젝트 삭제 요청(테스트용)")
	@ApiResponse(responseCode = "200", description = "PresignedUrl을 전송해 해당 url로 s3에 업로드된 파일을 삭제하는 요청", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
	public ResponseEntity<?> deleteObject(
		@RequestBody DeleteObjectByUrlRequestBody requestBody
	) {
		if (requestBody.getUrls() == null)
			return ResponseEntity.badRequest().body("null");

		List<String> urls = awsAuthService.abstractUrlFromPresignedUrl(requestBody.getUrls());
		List<String> keys = awsAuthService.abstractKeyFromUrl(urls);
		awsAuthService.deleteObjectByKey(keys);

		return ResponseEntity.ok("deleted");
	}

}
