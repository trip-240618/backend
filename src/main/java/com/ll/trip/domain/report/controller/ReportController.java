package com.ll.trip.domain.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.trip.dto.TripInfoDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "Report")
public class ReportController {
	@PostMapping("/create")
	@Operation(summary = "신고 생성")
	@ApiResponse(responseCode = "200", description = "신고 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<String> createReport(
		@RequestParam @Schema(example = "reply") String type,
		@RequestParam @Schema(example = "1") String typeId
	) {
		return ResponseEntity.ok("created");
	}


}
