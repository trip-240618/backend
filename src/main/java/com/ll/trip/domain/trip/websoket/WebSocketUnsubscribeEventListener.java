package com.ll.trip.domain.trip.websoket;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.ll.trip.domain.trip.planJ.service.PlanJEditService;
import com.ll.trip.domain.trip.planP.service.PlanPEditService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketUnsubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {

	private final PlanPEditService planPEditService;
	private final PlanJEditService planJEditService;

	@Override
	public void onApplicationEvent(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();

		if (destination != null) {
			String invitationCode = destination.substring(18);
			if (destination.startsWith("/topic/api/trip/p"))
				planPEditService.editorClosedSubscription(invitationCode, accessor.getUser().getName());
			else if (destination.startsWith("/topic/api/trip/j"))
				planJEditService.editorClosedSubscription(invitationCode, accessor.getUser().getName());
		}
	}
}
