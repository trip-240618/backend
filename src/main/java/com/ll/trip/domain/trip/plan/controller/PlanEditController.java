package com.ll.trip.domain.trip.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.dto.PlanEditResponseDto;
import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.plan.service.PlanEditService;
import com.ll.trip.global.security.userDetail.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/trip")
public class PlanEditController {
	private final PlanEditService planEditService;
	private final SimpMessagingTemplate template;

	// 주의!! planJ는 초대코드 + day임
	//messageMapping 설명용 getMapping 만들기
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

		template.convertAndSendToUser(username, "/topic/api/trip/" + invitationCode,
			new PlanResponseBody<>("edit", username)
		);
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
