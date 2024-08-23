package com.ll.trip.domain.trip.trip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.trip.dto.TripCreateDto;
import com.ll.trip.domain.trip.trip.dto.TripInfoDto;
import com.ll.trip.domain.trip.trip.dto.TripMemberDto;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
public class TripController {

	private final TripService tripService;

	@PostMapping("/create")
	@Operation(summary = "여행방 생성")
	@ApiResponse(responseCode = "200", description = "여행방 생성", content = {
		@Content(mediaType = "application/json",
			examples = @ExampleObject(value = "1A2B3C4D"))})
	public ResponseEntity<?> createTrip(
		@RequestBody TripCreateDto tripCreateDto,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		String invitationCode = tripService.generateInvitationCode();
		Long tripId = tripService.createTrip(tripCreateDto, invitationCode);

		tripService.joinTripById(tripId, securityUser.getId(), true);

		return ResponseEntity.ok(invitationCode);
	}

	@PostMapping("/join")
	@Operation(summary = "여행방 참가")
	@ApiResponse(responseCode = "200", description = "여행방 참가", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> joinTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @NotBlank @Parameter(description = "초대 코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		tripService.joinTripById(trip.getId(), securityUser.getId(), false);

		TripInfoDto response = new TripInfoDto(trip);
		List<TripMemberDto> tripMemberDtoList = tripService.findTripMemberUserByTripId(trip.getId());
		response.setTripMemberDtoList(tripMemberDtoList);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/enter")
	@Operation(summary = "여행방 입장")
	@ApiResponse(responseCode = "200", description = "여행방 입장", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = TripInfoDto.class))})
	public ResponseEntity<?> enterTripByInvitationCode(
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam @NotBlank @Parameter(description = "초대 코드", example = "1A2B3C4D") String invitationCode
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		if(!tripService.existTripMemberByTripIdAndUserId(trip.getId(), securityUser.getId())) return ResponseEntity.badRequest().body("입장 권한이 없습니다.");

		TripInfoDto response = new TripInfoDto(trip);
		List<TripMemberDto> tripMemberDtoList = tripService.findTripMemberUserByTripId(trip.getId());
		response.setTripMemberDtoList(tripMemberDtoList);

		return ResponseEntity.ok(response);
	}



}
