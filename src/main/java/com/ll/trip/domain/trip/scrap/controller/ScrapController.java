package com.ll.trip.domain.trip.scrap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.scrap.dto.ScrapCreateDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.service.ScrapService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "Scrap", description = "스크랩 API")
public class ScrapController {
	private final ScrapService scrapService;
	private final TripService tripService;

	@PostMapping("/{invitationCode}/scrap/create")
	@Operation(summary = "스크랩 생성")
	@ApiResponse(responseCode = "200", description = "스크랩 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScrapDetailDto.class))})
	public ResponseEntity<?> createScrap(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestBody ScrapCreateDto scrapCreateDto
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);

		if (tripService.existTripMemberByTripIdAndUserId(trip.getId(), securityUser.getId())) {
			Scrap scrap = scrapService.createScrap(
				securityUser.getId(), trip, scrapCreateDto.getTitle(), scrapCreateDto.getContent(),
				scrapCreateDto.getColor(), scrapCreateDto.isHasImage()
			);

			return ResponseEntity.ok(new ScrapDetailDto(
				scrap.getId(), securityUser.getUuid(), scrap.getTitle(),
				scrap.getContent(), scrap.isHasImage(), scrap.getColor(),
				false, scrap.getCreateDate()
			));
		}

		return ResponseEntity.badRequest().body("해당 여행방에 대한 권한이 없습니다.");
	}

	@PutMapping("/{invitationCode}/scrap/bookmark/toggle")
	@Operation(summary = "스크랩 생성")
	@ApiResponse(responseCode = "200", description = "스크랩 생성", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = ScrapDetailDto.class))})
	public ResponseEntity<?> toggleBookmark(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestParam @Parameter(description = "토글할 스크랩 pk", example = "1") long scrapId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		int update = scrapService.toggleScrapBookmark(securityUser.getId(), scrapId);
		if (update == 0) {
			long tripId = tripService.getTripIdByInvitationCode(invitationCode);
			scrapService.createScrapBookmark(securityUser.getId(), scrapId, tripId);
			return ResponseEntity.ok(true);
		}

		boolean response = scrapService.getIsToggleByUserIdAndScrapId(securityUser.getId(), scrapId);
		return ResponseEntity.ok(response);
	}
}
