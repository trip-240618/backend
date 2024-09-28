package com.ll.trip.domain.trip.planP.controller;

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

import com.ll.trip.domain.trip.planP.dto.PlanPCheckBoxResponseDto;
import com.ll.trip.domain.trip.planP.dto.PlanPCreateRequestDto;
import com.ll.trip.domain.trip.planP.dto.PlanPInfoDto;
import com.ll.trip.domain.trip.planP.dto.PlanPLockerDto;
import com.ll.trip.domain.trip.planP.dto.PlanPMoveDto;
import com.ll.trip.domain.trip.planP.entity.PlanP;
import com.ll.trip.domain.trip.planP.service.PlanPEditService;
import com.ll.trip.domain.trip.planP.service.PlanPService;
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
@RequestMapping("/trip/p")
@Slf4j
@Tag(name = "Plan P", description = "P타입 플랜 API")
public class PlanPController {
	private final PlanPService planPService;
	private final PlanPEditService planPEditService;
	private final SimpMessagingTemplate template;

	@PostMapping("/{tripId}/plan/create")
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
		@RequestBody PlanPCreateRequestDto requestDto
	) {
		PlanP plan = planPService.createPlanP(tripId, requestDto, securityUser.getUuid());
		PlanPInfoDto response = planPService.convertPlanPToDto(plan);

		template.convertAndSend(
			"/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("create", response)
		);

		return ResponseEntity.ok("created");
	}

	@GetMapping("/{tripId}/plan/list")
	@Operation(summary = "Plan 리스트 요청")
	@ApiResponse(responseCode = "200", description = "Plan 리스트 요청", content = {
		@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanPInfoDto.class)))})
	public ResponseEntity<?> showPlanPList(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "보관함 여부", example = "false") boolean locker
	) {
		List<PlanPInfoDto> response = planPService.findAllByTripId(tripId, locker);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{tripId}/plan/modify")
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

	@DeleteMapping("/{tripId}/plan/delete")
	@Operation(summary = "planP 삭제")
	@ApiResponse(responseCode = "200", description = "planP 삭제", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"delete\", \"data\": planId}"),
				@ExampleObject(name = "http 응답", value = "deleted")}
		)})
	public ResponseEntity<?> deletePlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestParam @Parameter(description = "plan pk", example = "1") Long planId
	) {
		planPService.deletePlanPByPlanId(planId);

		template.convertAndSend(
			"/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("delete", planId)
		);

		return ResponseEntity.ok("deleted");
	}

	@PutMapping("/{tripId}/plan/check")
	@Operation(summary = "planP 체크박스")
	@ApiResponse(responseCode = "200", description = "planP 체크박스 수정", content = {
		@Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "웹소켓 응답", value = "{\"command\": \"check\", \"data\": \"PlanPCheckBoxResponseDto\"}"),
				@ExampleObject(name = "http 응답", value = "checked")},
			schema = @Schema(implementation = PlanPCheckBoxResponseDto.class))})
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

	@MessageMapping("/{tripId}/edit/register")
	public void addEditor(
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable long tripId
	) {
		String username = headerAccessor.getUser().getName();
		log.info("uuid : " + username);

		String uuid = planPEditService.getEditorByTripId(tripId);
		if (uuid != null) {
			template.convertAndSendToUser(username, "/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("wait", uuid)
			);
			return;
		}

		planPEditService.addEditor(tripId, username);

		template.convertAndSend("/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("edit start", username)
		);
	}

	@GetMapping("/{tripId}/edit/register")
	@Operation(summary = "(웹소켓 설명용) 편집자 등록")
	@ApiResponse(responseCode = "200", description = "웹소켓으로 요청해야함, 편집자가 없을 시 편집자로 등록", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "편집자로 등록된 경우", value = "{\"command\": \"edit start\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
				@ExampleObject(name = "편집중인 사람이 존재할 경우", value = "{\"command\": \"wait\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}")
			}
		)})
	public SocketResponseBody<String> addEditor(
		@AuthenticationPrincipal SecurityUser securityUser,
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId
	) {
		template.convertAndSend("/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("edit start", securityUser.getUuid())
		);

		return new SocketResponseBody<>("edit start", "uuid");
	}

	@GetMapping("/{tripId}/edit/finish")
	@Operation(summary = "편집자 해제")
	@ApiResponse(responseCode = "200", description = "편집자 목록에서 제거", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(value = "{\"command\": \"edit finish\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
			}
		)})
	public SocketResponseBody<String> removeEditor(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		planPEditService.removeEditor(tripId, securityUser.getUuid());

		template.convertAndSend("/topic/api/trip/p/" + tripId,
			new SocketResponseBody<>("edit finish", securityUser.getUuid())
		);

		return new SocketResponseBody<>("edit finish", "uuid");
	}

	@PutMapping("/{tripId}/edit/move")
	@Operation(summary = "Plan P 이동")
	@ApiResponse(responseCode = "200", description = "Plan P 이동", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(value = "{\"command\": \"move\", \"data\": PlanPMoveDto}")},
			schema = @Schema(implementation = PlanPMoveDto.class)
		)})
	public ResponseEntity<?> movePlanP(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestBody PlanPMoveDto moveDto
	) {
		if (!planPEditService.isEditor(tripId, securityUser.getUuid()))
			return ResponseEntity.badRequest().body("편집자가 아닙니다.");

		planPEditService.movePlanByDayAndOrder(tripId, moveDto.getPlanId(), moveDto.getDayFrom(),
			moveDto.getDayTo(), moveDto.getOrderFrom(), moveDto.getOrderTo());

		template.convertAndSend("/topic/api/trip/p/" + tripId, new SocketResponseBody<>("move", moveDto));

		return ResponseEntity.ok("moved");
	}

	@PutMapping("/{tripId}/locker/move")
	@Operation(summary = "Plan P 보관함으로 이동 또는 일정으로 이동")
	@ApiResponse(responseCode = "200", description = "http응답은 \"moved\""
		, content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(description = "보관함으로 이동", value = "{\"command\": \"locker in\", \"data\": PlanPMoveDto}"),
				@ExampleObject(description = "일정으로 이동", value = "{\"command\": \"locker out\", \"data\": PlanPMoveDto}")
			},
			schema = @Schema(implementation = PlanPLockerDto.class)
		)})
	public ResponseEntity<?> moveByLocker(
		@PathVariable @Parameter(description = "트립 pk", example = "1", in = ParameterIn.PATH) long tripId,
		@RequestBody PlanPLockerDto lockerDto
	) {
		PlanPLockerDto response = planPService.moveLocker(tripId, lockerDto.getPlanId(), lockerDto.getDayTo(),
			lockerDto.isLocker());

		if (lockerDto.isLocker())
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("locker in", response));
		else
			template.convertAndSend("/topic/api/trip/p/" + tripId,
				new SocketResponseBody<>("locker out", response));

		return ResponseEntity.ok("moved");
	}

	@GetMapping("/show/editors")
	@Operation(summary = "플랜p editor권한 목록")
	@ApiResponse(responseCode = "200", description = "플랜p editor권한 목록")
	public ResponseEntity<?> showEditors() {
		return ResponseEntity.ok(planPEditService.getActiveEditTopicsAndUuid());
	}

}
