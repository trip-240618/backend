package com.ll.trip.domain.trip.planJ.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

import com.ll.trip.domain.notification.notification.service.NotificationService;
import com.ll.trip.domain.trip.planJ.dto.PlanJCreateRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJEditorRegisterDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJInfoDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJListDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJModifyRequestDto;
import com.ll.trip.domain.trip.planJ.dto.PlanJSwapRequestDto;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.service.PlanJEditService;
import com.ll.trip.domain.trip.planJ.service.PlanJService;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
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
@RequestMapping("/trip/{tripId}/plan/j")
@Tag(name = "Plan J", description = "J타입 플랜 API")
public class PlanJController {
	private final PlanJService planJService;
	private final PlanJEditService planJEditService;
	private final SimpMessagingTemplate template;
	private final NotificationService notificationService;

	@PostMapping("/create")
	@Operation(summary = "J형 Plan 생성")
	@ApiResponse(responseCode = "200", description = "J형 Plan생성, 응답데이터는 websocket으로 전송 (/topic/api/trip/j/{tripId})", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"create\", \"data\": \"PlanJInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "created")},
			schema = @Schema(implementation = PlanJInfoDto.class))})
	public ResponseEntity<?> createPlanJ(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanJCreateRequestDto requestDto
	) {
		int order = planJEditService.getLastOrderByTripId(tripId);

		PlanJ plan = planJService.createPlan(tripId, requestDto, order, securityUser.getUuid());
		PlanJInfoDto response = planJService.convertPlanJToDto(plan);

		if (!requestDto.isLocker())
			template.convertAndSend(
				"/topic/api/trip/j/" + tripId,
				new SocketResponseBody<>("create", response)
			);
		else {
			return ResponseEntity.ok(response);
		}

		notificationService.createPlanCreateNotification(tripId);
		return ResponseEntity.ok("created");
	}

	@GetMapping("/list")
	@Operation(summary = "PlanJ 리스트 요청")
	@ApiResponse(responseCode = "200", description = "Plan 리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanJInfoDto.class)))})
	public ResponseEntity<?> showPlanJList(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "day", example = "1") int day,
		@RequestParam @Parameter(description = "보관함 여부", example = "false") boolean locker
	) {
		List<PlanJListDto> response;
		if (!locker)
			response = planJService.findAllPlanAByTripIdAndDay(tripId, day);
		else
			response = planJService.findAllPlanBByTripId(tripId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/edit/modify")
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
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody PlanJModifyRequestDto requestBody
	) {
		PlanJ plan = planJService.findPlanJById(requestBody.getPlanId());
		int order = plan.getOrderByDate();
		Integer dayFrom = plan.getDayAfterStart();
		Integer dayTo = requestBody.getDayAfterStart();

		if (!plan.getStartTime().equals(requestBody.getStartTime()) || !dayFrom.equals(dayTo)) {
			if (!requestBody.isLocker()) {
				planJEditService.checkHasEditor(tripId, dayFrom, securityUser.getUuid());
				if (!dayTo.equals(dayFrom)) {
					planJEditService.checkHasEditor(tripId, dayTo, securityUser.getUuid());
				}
			}
			order = planJEditService.getLastOrderByTripId(tripId);
			notificationService.createPlanMoveNotification(tripId);
		}

		plan = planJService.updatePlanJByPlanId(plan, requestBody, order);
		PlanJInfoDto response = planJService.convertPlanJToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/j/" + tripId,
			new SocketResponseBody<>("modify", response)
		);

		return ResponseEntity.ok("modified");
	}

	@PutMapping("/edit/swap")
	@Operation(summary = "PlanJ 스왑")
	@ApiResponse(responseCode = "200", description = "PlanJ 스왑", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"swap\", \"data\": \"[ PlanJListDto ]\"}"),
				@ExampleObject(name = "http 응답", value = "swapped")},
			array = @ArraySchema(schema = @Schema(implementation = PlanJListDto.class)))})
	public ResponseEntity<?> swapPlanJ(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody PlanJSwapRequestDto requestBody
	) {
		int day = requestBody.getDayAfterStart();

		planJEditService.checkIsEditor(tripId, day, securityUser.getUuid());
		List<PlanJListDto> response = planJService.bulkUpdatePlanJOrder(tripId, requestBody.getDayAfterStart(),
			requestBody.getOrderDtos());

		template.convertAndSend(
			"/topic/api/trip/j/" + tripId,
			new SocketResponseBody<>("swap", response)
		);

		notificationService.createPlanMoveNotification(tripId);
		return ResponseEntity.ok("swapped");
	}

	@DeleteMapping("/delete")
	@Operation(summary = "PlanJ 삭제")
	@ApiResponse(responseCode = "200", description = "PlanJ 삭제", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\n  \"command\": \"delete\",\n  \"data\": {\n    \"dayAfterStart\": 1,\n    \"planId\": 1\n  }\n}"),
				@ExampleObject(name = "http 응답", value = "deleted")}
		)})
	public ResponseEntity<?> deletePlanJ(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId,
		@RequestParam @Parameter(description = "dayAfterStart", example = "1") Integer day
	) {
		planJService.deletePlanJById(day, planId);

		template.convertAndSend(
			"/topic/api/trip/j/" + tripId,
			new SocketResponseBody<>("delete", Map.of("dayAfterStart", day, "planId", planId))
		);

		return ResponseEntity.ok("deleted");
	}

	@MessageMapping("/trip/{tripId}/plan/j/{day}/edit/register")
	public void addEditor(
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable long tripId,
		@DestinationVariable int day
	) {
		String sessionId = headerAccessor.getSessionId();
		String nickname = (String)Objects.requireNonNull(headerAccessor.getSessionAttributes())
			.getOrDefault("nickname", null);
		String uuid = Objects.requireNonNull(headerAccessor.getUser()).getName();

		log.info("sessionId: " + sessionId);
		log.info("nickname: " + nickname);
		String[] editor = planJEditService.getEditorByTripIdAndDay(tripId, day);
		if (editor != null) {
			template.convertAndSend("/topic/api/trip/j/" + tripId,
				new SocketResponseBody<>("wait", new PlanJEditorRegisterDto(day, editor[1], editor[2]))
			);
			return;
		}

		planJEditService.addEditor(tripId, day, sessionId, uuid, nickname);

		template.convertAndSend("/topic/api/trip/j/" + tripId,
			new SocketResponseBody<>("edit start", new PlanJEditorRegisterDto(day, uuid, nickname))
		);
	}

	@GetMapping("/{day}/edit/register")
	@Operation(summary = "(웹소켓 설명용) 편집자 등록")
	@ApiResponse(responseCode = "200", description = "웹소켓으로 요청해야함, 편집자가 없을 시 편집자로 등록", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "편집자로 등록된 경우", value = "{\"command\": \"edit start\", \"data\": {\"day\": 1, \"editorUuid\": \"123e4567-e89b-12d3-a456-426614174000\", \"nickname\": \"등록된 편집자\"}}"),
				@ExampleObject(name = "편집중인 사람이 존재할 경우", value = "{\"command\": \"wait\", \"data\": {\"day\": 1, \"editorUuid\": \"123e4567-e89b-12d3-a456-426614174000\", \"nickname\": \"기존 편집자\"}}")
			},
			schema = @Schema(implementation = PlanJEditorRegisterDto.class))})
	public SocketResponseBody<String> addEditor(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable @Parameter(description = "day", example = "1", in = ParameterIn.PATH) int day
	) {
		return new SocketResponseBody<>("edit start", "uuid");
	}

	@GetMapping("/{day}/edit/finish")
	@Operation(summary = "편집자 해제")
	@ApiResponse(responseCode = "200", description = "편집자 목록에서 제거", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(value = "{\"command\": \"edit finish\", \"data\": {\"day\": 1, \"editorUuid\": \"123e4567-e89b-12d3-a456-426614174000\", \"nickname\": \"example\"}}")
			})})
	public void removeEditor(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@PathVariable int day
	) {
		String uuid = securityUser.getUuid();
		log.info("uuid : " + uuid);

		planJEditService.removeEditorByDestination(tripId, day, uuid);
	}

	@GetMapping("/show/editors")
	@Operation(summary = "플랜j editor권한 목록")
	@ApiResponse(responseCode = "200", description = "플랜j editor권한 목록")
	public ResponseEntity<?> showEditors() {
		return ResponseEntity.ok(planJEditService.getSessionIdMap());
	}

}
