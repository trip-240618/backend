package com.ll.trip.domain.trip.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.plan.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.plan.entity.PlanP;
import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.plan.service.PlanPService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/p")
public class PlanController {

	private final TripService tripService;
	private final PlanPService planPService;
	private final SimpMessagingTemplate template;

	@PostMapping("/{invitationCode}/plan/create")
	public ResponseEntity<?> createPlanP(
		@PathVariable String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanPCreateRequestDto requestDto
	){
		Trip trip = tripService.findByInvitationCode(invitationCode);
		PlanP plan = planPService.createPlanP(trip, requestDto, securityUser.getUuid());
		PlanPInfoDto response = planPService.convertPlanPToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/" + invitationCode,
			new PlanResponseBody<>("create", response)
		);

		return ResponseEntity.ok("created");
	}
}
