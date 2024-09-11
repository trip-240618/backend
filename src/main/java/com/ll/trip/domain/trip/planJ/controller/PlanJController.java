package com.ll.trip.domain.trip.planJ.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJModifyRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJSwapRequestDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.service.PlanJEditService;
import com.ll.trip.domain.trip.planJ.service.PlanJService;
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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip/j")
@Tag(name = "Plan J", description = "J타입 플랜의 CRUD 기능")
public class PlanJController {
	private final TripService tripService;
	private final PlanJService planJService;
	private final PlanJEditService planJEditService;
	private final SimpMessagingTemplate template;

	@PostMapping("/{invitationCode}/plan/create")
	@Operation(summary = "J형 Plan 생성")
	@ApiResponse(responseCode = "200", description = "J형 Plan생성, 응답데이터는 websocket으로 전송", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"create\", \"data\": \"PlanJInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "created")},
			schema = @Schema(implementation = PlanJInfoDto.class))})
	public ResponseEntity<?> createPlanJ(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanJCreateRequestDto requestDto
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		int order = planJEditService.getLastOrderByTripId(trip.getId(), requestDto.getDayAfterStart());

		PlanJ plan = planJService.createPlan(trip, requestDto, order, securityUser.getUuid());
		PlanJInfoDto response = planJService.convertPlanJToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/j/" + invitationCode,
			new PlanResponseBody<>("create", response)
		);

		return ResponseEntity.ok("created");
	}

	@GetMapping("/{invitationCode}/plan/list")
	@Operation(summary = "Plan 리스트 요청")
	@ApiResponse(responseCode = "200", description = "Plan 리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanJInfoDto.class)))})
	public ResponseEntity<?> showPlanJList(
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestParam @Parameter(description = "day", example = "1") int day,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Trip trip = tripService.findByInvitationCode(invitationCode);
		List<PlanJInfoDto> response = planJService.findAllByTripIdAndDay(trip.getId(), day);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{invitationCode}/plan/edit/modify")
	@Operation(summary = "PlanJ 수정")
	@ApiResponse(responseCode = "200", description = "PlanJ 수정", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"modify\", \"data\": \"PlanJInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "modified")},
			schema = @Schema(implementation = PlanJInfoDto.class))})
	public ResponseEntity<?> modifyPlanJ(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestBody PlanJModifyRequestDto requestBody
	) {
		PlanJ plan = planJService.findPlanJById(requestBody.getPlanId());
		int order = plan.getOrderByDate();
		int dayFrom = plan.getDayAfterStart();
		int dayTo = requestBody.getDayAfterStart();

		if (plan.getStartTime() != requestBody.getStartTime() || dayFrom != dayTo) {
			if (!planJEditService.isEditor(invitationCode, securityUser.getUuid(), dayFrom))
				return ResponseEntity.badRequest().body("day" + dayFrom + "의 편집자가 아닙니다.");
			if (dayTo != dayFrom && !planJEditService.isEditor(invitationCode, securityUser.getUuid(), dayTo))
				return ResponseEntity.badRequest().body("day" + dayTo + "의 편집자가 아닙니다.");

			Trip trip = tripService.findByInvitationCode(invitationCode);
			order = planJEditService.getLastOrderByTripId(trip.getId(), requestBody.getDayAfterStart());

		}

		plan = planJService.updatePlanJByPlanId(plan, requestBody, order);
		PlanJInfoDto response = planJService.convertPlanJToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/j/" + invitationCode,
			new PlanResponseBody<>("modify", response)
		);

		return ResponseEntity.ok("modified");
	}

	@PutMapping("/{invitationCode}/plan/edit/swap")
	@Operation(summary = "PlanJ 스왑")
	@ApiResponse(responseCode = "200", description = "PlanJ 스왑", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"swap\", \"data\": \"PlanJSwapDto\"}"),
				@ExampleObject(name = "http 응답", value = "swapped")},
			schema = @Schema(implementation = PlanJInfoDto.class))})
	public ResponseEntity<?> swapPlanJ(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestBody PlanJSwapRequestDto requestBody
	) {
		int day = requestBody.getDayAfterStart();

		if (!planJEditService.isEditor(invitationCode, securityUser.getUuid(), day))
			return ResponseEntity.badRequest().body("day" + day + "의 편집자가 아닙니다.");

		if(planJEditService.swapPlanJByIds(requestBody.getPlanId1(), requestBody.getPlanId2()) != 2)
			return ResponseEntity.internalServerError().body("swap 실패");


		template.convertAndSend(
			"/topic/api/trip/j/" + invitationCode,
			new PlanResponseBody<>("modify", requestBody)
		);

		return ResponseEntity.ok("swapped");
	}

	@DeleteMapping("/{invitationCode}/plan/delete")
	@Operation(summary = "PlanJ 삭제")
	@ApiResponse(responseCode = "200", description = "PlanJ 삭제", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"delete\", \"data\": planId}"),
				@ExampleObject(name = "http 응답", value = "삭제로 인해 순서가 수정된 plan 수")}
		)})
	public ResponseEntity<?> deletePlanJ(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "초대코드", example = "1A2B3C4D", in = ParameterIn.PATH) String invitationCode,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId
	) {
		planJService.deletePlanJById(planId);

		template.convertAndSend(
			"/topic/api/trip/j/" + invitationCode,
			new PlanResponseBody<>("delete", planId)
		);

		return ResponseEntity.ok("deleted");
	}

	@MessageMapping("/j/{invitationCode}/{day}/edit/register")
	//TODO messageMapping 설명용 getMapping 만들기
	public void addEditor(
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable String invitationCode,
		@DestinationVariable int day
	) {
		String username = headerAccessor.getUser().getName();
		log.info("uuid : " + username);

		String uuid = planJEditService.getEditorByInvitationCodeAndDay(invitationCode, day);
		if (uuid != null) {
			template.convertAndSendToUser(username, "/topic/api/trip/j/" + invitationCode,
				new PlanResponseBody<>("wait", uuid)
			);
			return;
		}

		planJEditService.addEditor(invitationCode, username, day);

		template.convertAndSend("/topic/api/trip/j/" + invitationCode,
			new PlanResponseBody<>("edit start", username)
		);
	}

	@GetMapping("/j/{invitationCode}/{day}/edit/register")
	@Operation(summary = "(웹소켓 설명용) 편집자 등록")
	@ApiResponse(responseCode = "200", description = "웹소켓으로 요청해야함, 편집자가 없을 시 편집자로 등록", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "편집자로 등록된 경우", value = "{\"command\": \"edit start\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
				@ExampleObject(name = "편집중인 사람이 존재할 경우", value = "{\"command\": \"wait\", \"data\": \"223e4567-e89b-12d3-a456-426614174001\"}")
			}
		)})
	public PlanResponseBody<String> addEditor(
		@PathVariable String invitationCode,
		@PathVariable int day
	) {
		return new PlanResponseBody<>("edit start", "uuid");
	}

}
