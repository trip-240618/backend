package com.ll.trip.domain.trip.websoket;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.ll.trip.domain.trip.planJ.service.PlanJEditService;
import com.ll.trip.domain.trip.planP.service.PlanPEditService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

	private final PlanPEditService planPEditService;
	private final PlanJEditService planJEditService;

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId(); // 세션 ID로 연결된 클라이언트 식별

		log.info("클라이언트가 연결을 종료했습니다. 세션 ID: " + sessionId);
		String username = (accessor.getUser() != null) ? accessor.getUser().getName() : "Unknown user";

		planPEditService.removeEditorBySessionId(sessionId);
		planJEditService.removeEditorBySessionId(sessionId);
	}
}
