package com.ll.trip.domain.trip.plan.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanEditService {
	//planJ는 초대코드 + day로 구독
	//planP는 초대코드로 구독
	private final String TOPIC_PREFIX = "/topic/api/trip/edit/";
	private final ConcurrentHashMap<String, String> activeEditTopics = new ConcurrentHashMap<>();

	public void decrementSubscription(String destination, String sessionId) {
		String invitationCode = destination.substring(TOPIC_PREFIX.length());
		if (activeEditTopics.contains(invitationCode) &&
			activeEditTopics.get(invitationCode).equals(sessionId)) {
			activeEditTopics.remove(invitationCode);
		}
	}

	public void addEditor(String invitationCode, String sessionId) {
		activeEditTopics.put(invitationCode, sessionId);
	}

	public boolean isEditing(String invitationCode) {
		return activeEditTopics.contains(invitationCode);
	}
}
