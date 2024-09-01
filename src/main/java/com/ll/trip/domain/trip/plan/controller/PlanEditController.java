package com.ll.trip.domain.trip.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.dto.PlanEditResponseDto;
import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.plan.service.PlanEditService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
@Tag(name = "Plan Edit", description = "edit 시작 부터 종료까지의 기능 (웹소켓요청의 경우 설명용 get 매핑이 있음)")
public class PlanEditController {
	private final PlanEditService planEditService;
	private final SimpMessagingTemplate template;

	//TODO messageMapping 설명용 getMapping 만들기
	@MessageMapping("/{invitationCode}/edit/register")
	public void addEditor(
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable String invitationCode
	) {
		String username = headerAccessor.getUser().getName();
		log.info("uuid : " + username);

		String uuid = planEditService.getEditorByInvitationCode(invitationCode);
		if (uuid != null) {
			template.convertAndSendToUser(username, "/topic/api/trip/",
				new PlanResponseBody<>("wait", uuid)
			);
			return;
		}

		planEditService.addEditor(invitationCode, username);

		template.convertAndSend("/topic/api/trip/" + invitationCode,
			new PlanResponseBody<>("edit start", username)
		);
	}

	@GetMapping("/{invitaionCode}/edit/register")
	@Operation(summary = "(웹소켓 설명용) 편집자 등록")
	@ApiResponse(responseCode = "200", description = "편집자가 없을 시 편집자로 등록", content = {
		@Content(mediaType = "application/json",
			examples = {
				@ExampleObject(name = "편집자로 등록된 경우", value = "{\"command\": \"edit start\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}"),
				@ExampleObject(name = "편집중인 사람이 존재할 경우", value = "{\"command\": \"wait\", \"data\": \"123e4567-e89b-12d3-a456-426614174000\"}")
			}
			)})
	public PlanResponseBody<String> addEditor () {
		return new PlanResponseBody<>("edit start", "uuid");
	}

	@PutMapping("/{invitationCode}/edit/move")
	public ResponseEntity<?> movePlanP(
		@PathVariable String invitationCode,
		@AuthenticationPrincipal SecurityUser securityUser,
		@RequestParam long planId,
		@RequestParam int dayTo,
		@RequestParam int orderTo
	) {
		if (!planEditService.isEditor(invitationCode, securityUser.getUuid()))  return ResponseEntity.badRequest().body("편집자가 아닙니다.");

		int updatedCount = planEditService.movePlanByDayAndOrder(planId, dayTo, orderTo);
		PlanEditResponseDto response = new PlanEditResponseDto(planId, dayTo, orderTo, updatedCount);

		template.convertAndSend("/topic/api/trip/" + invitationCode, new PlanResponseBody<>("moved", response));

		return ResponseEntity.ok("moved");
	}

}
