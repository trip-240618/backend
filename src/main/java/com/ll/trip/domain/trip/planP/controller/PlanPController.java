package com.ll.trip.domain.trip.planP.controller;

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
import com.ll.trip.domain.trip.planP.dto.PlanPCheckBoxResponseDto;
import com.ll.trip.domain.trip.planP.dto.PlanPEditRegisterDto;
import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.dto.PlanPOrderDto;
import com.ll.trip.domain.trip.planP.dto.PlanPWeekDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;
import com.ll.trip.domain.trip.planP.service.PlanPEditService;
import com.ll.trip.domain.trip.planP.service.PlanPService;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/{tripId}/plan/p")
@Slf4j
@Tag(name = "Plan P", description = "P타입 플랜 API")
public class PlanPController {
	private final PlanPService planPService;
	private final PlanPEditService planPEditService;
	private final SimpMessagingTemplate template;
	private final NotificationService notificationService;

	@PostMapping("/create")
	@Operation(summary = "P형 Plan 생성")
	@ApiResponse(responseCode = "200", description = "P형 Plan생성, 응답데이터는 websocket으로 전송", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"create\", \"data\": \"PlanPInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "created")},
			schema = @Schema(implementation = PlanPInfoDto.class))})
	public ResponseEntity<?> createPlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanPInfoDto requestDto
	) {
		PlanP plan = planPService.createPlanP(tripId, requestDto);
		PlanPInfoDto response = planPService.convertPlanPToDto(plan);

		if (!response.isLocker())
			template.convertAndSend(
				"/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("create", response)
			);

		notificationService.createPlanCreateNotification(tripId);
		return ResponseEntity.ok("created");
	}

	@GetMapping("/list")
	@Operation(summary = "Plan 리스트 요청")
	public ResponseEntity<PlanPWeekDto<PlanPInfoDto>> showPlanPList(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam(required = false) @Parameter(description = "주차(플랜B일 땐 안줘도 됌)", example = "1") Integer week,
		@RequestParam @Parameter(description = "보관함 여부", example = "false") boolean locker
	) {
		PlanPWeekDto<PlanPInfoDto> response = new PlanPWeekDto<>(week, planPService.findAllByTripId(tripId, week, locker));
		return ResponseEntity.ok(response);
	}

	@PutMapping("/modify")
	@Operation(summary = "planP 수정")
	@ApiResponse(responseCode = "200", description = "planP 수정", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"modify\", \"data\": \"PlanPInfoDto\"}"),
				@ExampleObject(name = "http 응답", value = "modified")},
			schema = @Schema(implementation = PlanPInfoDto.class))})
	public ResponseEntity<?> modifyPlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody PlanPInfoDto requestBody
	) {
		PlanP plan = planPService.updatePlanPByPlanId(requestBody);
		PlanPInfoDto response = planPService.convertPlanPToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("modify", response)
		);

		return ResponseEntity.ok("modified");
	}

	@DeleteMapping("/delete")
	@Operation(summary = "planP 삭제")
	@ApiResponse(responseCode = "200", description = "planP 삭제", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\n  \"command\": \"delete\",\n  \"data\": {\n    \"dayAfterStart\": 1,\n    \"planId\": 1\n  }\n}"),
				@ExampleObject(name = "http 응답", value = "deleted")}
		)})
	public ResponseEntity<?> deletePlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId,
		@RequestParam @Parameter(description = "dayAfterStart", example = "1") Integer day
	) {
		planPService.deletePlanPByPlanId(day, planId);

		template.convertAndSend(
			"/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("delete", Map.of("dayAfterStart", day, "planId", planId))
		);

		return ResponseEntity.ok("deleted");
	}

	@PutMapping("/check")
	@Operation(summary = "planP 체크박스")
	@ApiResponse(responseCode = "200", description = "planP 체크박스 수정", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"check\", \"data\": \"PlanPCheckBoxResponseDto\"}"),
				@ExampleObject(name = "http 응답", value = "checked")}
			, schema = @Schema(implementation = PlanPCheckBoxResponseDto.class)
		)})
	public ResponseEntity<?> modifyPlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId
	) {
		PlanPCheckBoxResponseDto response = planPService.updateCheckBoxById(planId);

		template.convertAndSend(
			"/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("check", response)
		);

		return ResponseEntity.ok("checked");
	}

	@MessageMapping("trip/{tripId}/plan/p/edit/register")
	public void addEditor(
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable long tripId
	) {
		String sessionId = headerAccessor.getSessionId();
		String nickname = (String)Objects.requireNonNull(headerAccessor.getSessionAttributes())
			.getOrDefault("nickname", null);
		String uuid = Objects.requireNonNull(headerAccessor.getUser()).getName();
		log.info("uuid: " + uuid);
		log.info("nickname: " + nickname);

		String[] editor = planPEditService.getEditorByTripId(tripId);
		if (editor != null) {
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("wait", new PlanPEditRegisterDto(editor[1], editor[2]))
			);
			return;
		}

		planPEditService.addEditor(tripId, sessionId, uuid, nickname);

		template.convertAndSend("/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("edit start", new PlanPEditRegisterDto(uuid, nickname))
		);
	}

	@GetMapping("/edit/register")
	@Operation(summary = "(웹소켓 설명용) 편집자 등록")
	@ApiResponse(responseCode = "200", description = "웹소켓으로 요청해야함, 편집자가 없을 시 편집자로 등록", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "편집자로 등록된 경우", value = "{\"command\": \"edit start\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
				@ExampleObject(name = "편집중인 사람이 존재할 경우", value = "{\"command\": \"wait\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}")
			}
		)})
	public ResponseEntity<SocketResponseBody<PlanPEditRegisterDto>> addEditor(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId
	) {

		return ResponseEntity.ok(new SocketResponseBody<>("edit finish",
			new PlanPEditRegisterDto("uuid", "nickname")));
	}

	@GetMapping("/edit/finish")
	@Operation(summary = "편집자 해제")
	@ApiResponse(responseCode = "200", description = "편집자 목록에서 제거", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(value = "{\"command\": \"edit finish\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
			}
		)})
	public void removeEditor(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		planPEditService.removeEditorByDestination(tripId, securityUser.getUuid());
	}

	@PutMapping("/edit/move")
	@Operation(summary = "Plan P 이동")
	@ApiResponse(responseCode = "200", description = "Plan P 이동", content = {
		@Content(mediaType = "application/json", examples = {
			@ExampleObject(description = "웹소켓 응답",
				value = "{\"command\": \"move\", \"data\": PlanPWeekDto}"),
			@ExampleObject(description = "http 응답",
				value = "moved")
		}, schema = @Schema(implementation = PlanPWeekDto.class)
		)})
	public ResponseEntity<?> movePlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanPWeekDto<PlanPOrderDto> requestDto
	) {
		planPEditService.checkIsEditor(tripId, securityUser.getUuid());

		planPService.movePlanByDayAndOrder(tripId, requestDto);

		notificationService.createPlanMoveNotification(tripId);
		return ResponseEntity.ok("moved");
	}

	@PutMapping("/locker/move")
	@Operation(summary = "Plan P 보관함으로 이동 또는 일정으로 이동")
	@ApiResponse(responseCode = "200", description = "http응답은 \"moved\""
		, content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(description = "보관함으로 이동", value = "{\n  \"command\": \"delete\",\n  \"data\": {\n    \"dayAfterStart\": 1,\n    \"planId\": 1\n  }\n}"),
				@ExampleObject(description = "일정으로 이동", value = "{\"command\": \"create\", \"data\": PlanPInfoDto}")},
			schema = @Schema(implementation = PlanPOrderDto.class)
		)})
	public ResponseEntity<?> moveByLocker(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody PlanPInfoDto request
	) {
		planPService.moveLocker(tripId, request.getPlanId(), request.getDayAfterStart(),
			request.isLocker());

		return ResponseEntity.ok("moved");
	}

	@GetMapping("/show/editors")
	@Operation(summary = "플랜p editor권한 목록")
	@ApiResponse(responseCode = "200", description = "플랜p editor권한 목록")
	public ResponseEntity<?> showEditors(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId
	) {
		return ResponseEntity.ok(planPEditService.getSessionIdMap());
	}

}
