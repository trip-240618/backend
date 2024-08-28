package com.ll.trip.domain.trip.plan.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.trip.plan.response.PlanResponseBody;
import com.ll.trip.domain.trip.plan.service.PlanEditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/edit")
public class PlanEditController {
	private final PlanEditService planEditService;


	// 주의!! planJ는 초대코드 + day임
	@MessageMapping("/{invitationCode}/connect")
	@SendTo("/topic/trip/{invitationCode}")
	public PlanResponseBody<String> addEditor(
		@Payload String uuid,
		SimpMessageHeaderAccessor headerAccessor,
		@DestinationVariable String invitationCode
		) {
		if(planEditService.isEditing(invitationCode)) {
			return new PlanResponseBody<>("wait", uuid);
		}

		String sessionId = headerAccessor.getSessionId();
		planEditService.addEditor(invitationCode, sessionId);
		return new PlanResponseBody<>("edit", uuid);
	}
	//messageMapping 설명용 getMapping 만들기


}
