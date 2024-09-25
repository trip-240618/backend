package com.ll.trip.domain.trip.scrap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.scrap.dto.ScrapCreateDto;
import com.ll.trip.domain.trip.scrap.dto.ScrapDetailDto;
import com.ll.trip.domain.trip.scrap.entity.Scrap;
import com.ll.trip.domain.trip.scrap.service.ScrapService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "Scrap", description = "스크랩 API")
public class ScrapController {
	private ScrapService scrapService;
	private TripService tripService;
	private UserService userService;

	@PostMapping("/{invitationCode}/scrap/create")
	public ResponseEntity<?> createScrap(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable String invitationCode,
		@RequestBody ScrapCreateDto scrapCreateDto
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		UserEntity user = userService.findUserByUserId(securityUser.getId());

		if (tripService.existTripMemberByTripIdAndUserId(trip.getId(), user.getId())) {
			Scrap scrap = scrapService.createScrap(
				user, trip, scrapCreateDto.getTitle(), scrapCreateDto.getContent(),
				scrapCreateDto.getColor(), scrapCreateDto.isHasImage()
			);

			return ResponseEntity.ok(new ScrapDetailDto(
				scrap.getId(), user.getUuid(), scrap.getTitle(),
				scrap.getContent(), scrap.isHasImage(), scrap.getColor(), false
			));
		}

		return ResponseEntity.badRequest().body("해당 여행방에 대한 권한이 없습니다.");
	}

	public ResponseEntity<?> toggleBookmark(

	) {
		return ResponseEntity.ok("");
	}
}
