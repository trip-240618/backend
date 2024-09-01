package com.ll.trip.domain.trip.plan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.plan.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.plan.entity.PlanP;
import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.plan.service.PlanPService;
import com.ll.trip.domain.trip.trip.entity.Trip;
import com.ll.trip.domain.trip.trip.service.TripService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/p")
@Tag(name = "Plan P", description = "P타입 플랜의 CRUD 기능 (순서변경 제외)")
public class PlanPController {

	private final TripService tripService;
	private final PlanPService planPService;
	private final SimpMessagingTemplate template;

	@PostMapping("/{invitationCode}/plan/create")
	@Operation(summary = "P형 Plan 생성")
	@ApiResponse(responseCode = "200", description = "P형 Plan생성, 응답데이터는 websocket으로 전송", content = {
		@Content(mediaType = "application/json",
			examples = {
			@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"create\", \"data\": \"PlanPInfoDto\"}"),
			@ExampleObject(name = "http 응답", value = "created")},
			schema = @Schema(implementation = PlanPInfoDto.class))})
	public ResponseEntity<?> createPlanP(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D" ,in = ParameterIn.PATH) String invitationCode,
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

	@GetMapping("/{invitationCode}/plan/list")
	@Operation(summary = "Plan 리스트 요청")
	@ApiResponse(responseCode = "200", description = "Plan 리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanPInfoDto.class)))})
	public ResponseEntity<?> showPlanPList(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D" ,in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser
	){
		Trip trip = tripService.findByInvitationCode(invitationCode);
		List<PlanP> response = planPService.findAllByTripId(trip.getId());

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{invitationCode}/plan/modify")
	@Operation(summary = "planP 수정")
	@ApiResponse(responseCode = "200", description = "planP 수정", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"modify\", \"data\": \"PlanPInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "modified")},
			schema = @Schema(implementation = PlanPInfoDto.class))})
	public ResponseEntity<?> modifyPlanP(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId,
		@RequestBody PlanPInfoDto requestBody
	){
		PlanP plan = planPService.updatePlanPByPlanId(planId, requestBody);
		PlanPInfoDto response = planPService.convertPlanPToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/" + invitationCode,
			new PlanResponseBody<>("modify", response)
		);

		return ResponseEntity.ok("modified");
	}

	@DeleteMapping("/{invitationCode}/plan/delete")
	@Operation(summary = "planP 수정")
	@ApiResponse(responseCode = "200", description = "planP 수정", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"delete\", \"data\": planId}"),
				@ExampleObject(name = "http 응답", value = "삭제로 인해 순서가 수정된 plan 수")}
		)})
	public ResponseEntity<?> deletePlanP(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId
	){
		int modifiedOrder = planPService.deletePlanPByPlanId(planId);

		template.convertAndSend(
			"/topic/api/trip/" + invitationCode,
			new PlanResponseBody<>("delete", planId)
		);

		return ResponseEntity.ok(modifiedOrder);
	}




}
