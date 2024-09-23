package com.ll.trip.domain.version.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.version.entity.Version;
import com.ll.trip.domain.version.service.VersionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Version", description = "앱 버전 API")
public class VersionController {
	private final VersionService versionService;

	@GetMapping("/version/last")
	@Operation(summary = "최신 버전 정보")
	@ApiResponse(responseCode = "200", description = "최신 버전 정보 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = Version.class))})
	public ResponseEntity<?> getLastVersion(){
		Version version = versionService.getLastVersion();
		return ResponseEntity.ok(version);
	}

}
