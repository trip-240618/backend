package com.ll.trip.domain.trip.location.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.location.dto.AutoCompleteRequestDto;
import com.ll.trip.domain.trip.location.dto.AutoCompleteResponseDto;
import com.ll.trip.domain.trip.location.dto.PlaceDetailResponse;
import com.ll.trip.domain.trip.location.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip/location")
@Tag(name = "Trip Location", description = "장소 검색 API")
public class LocationController {

	private final LocationService locationService;

	@PostMapping("/autocomplete")
	@Operation(summary = "주소 자동완성")
	@ApiResponse(responseCode = "200", description = "주소 자동완성", content = {
		@Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = AutoCompleteResponseDto.class)))})
	public ResponseEntity<?> showAutocomplete(
		@RequestBody AutoCompleteRequestDto requestDto
	){
		List<AutoCompleteResponseDto> response = locationService.getAutoComplete(requestDto);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/place/{placeId}")
	@Operation(summary = "장소 세부정보")
	@ApiResponse(responseCode = "200", description = "위경도를 포함한 장소 세부정보", content = {
		@Content(mediaType = "application/json",
			schema = @Schema(implementation = PlaceDetailResponse.class))})
	public ResponseEntity<?> showPlaceDetail(
		@PathVariable String placeId
	){
		PlaceDetailResponse response = locationService.getPlaceDetail(placeId);

		return ResponseEntity.ok(response);
	}



}
