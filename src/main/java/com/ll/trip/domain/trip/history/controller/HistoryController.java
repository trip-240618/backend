package com.ll.trip.domain.trip.history.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.history.dto.HistoryCreateRequestDto;
import com.ll.trip.domain.trip.history.dto.HistoryListDto;
import com.ll.trip.domain.trip.history.service.HistoryService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "History", description = "History 기능")
public class HistoryController {

	private final TripService tripService;
	private final UserService userService;
	private final HistoryService historyService;

	@GetMapping("/{invitationCode}/history/list")
	public ResponseEntity<?> showHistoryList(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		if (!tripService.existTripMemberByTripInvitationCodeAndUserId(invitationCode, securityUser.getId()))
			return ResponseEntity.badRequest().body("권한이 없습니다.");

		Trip trip = tripService.findByInvitationCode(invitationCode);
		List<HistoryListDto> response = historyService.findAllByTripId(trip.getId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{invitationCode}/history/create")
	public ResponseEntity<?> createHistory(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody HistoryCreateRequestDto requestDto
	) {
		UserEntity user = userService.findUserByUserId(securityUser.getId());
		Trip trip = tripService.findByInvitationCode(invitationCode);
		historyService.createHistory(requestDto, user, trip);

		return ResponseEntity.ok("created");
	}

}
