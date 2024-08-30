package com.ll.trip.domain.trip.plan.service;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketUnsubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {

	private final PlanEditService planEditService;

	@Override
	public void onApplicationEvent(SessionUnsubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = accessor.getDestination();

		if (destination != null && destination.startsWith("/topic/api/trip/")) {
			planEditService.decrementSubscription(destination, accessor.getUser().getName());
		}
	}
}
