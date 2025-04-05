package com.ll.trip.domain.trip.plan.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.dto.PlanJEditorRegisterDto;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.global.handler.exception.PermissionDeniedException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlanEditService {
	private final PlanJRepository planJRepository;
	@Getter
	private final ConcurrentHashMap<String, String> sessionIdMap = new ConcurrentHashMap<>();
	// sessionId, type/tripId?value
	@Getter
	private final ConcurrentHashMap<String, String[]> destinationMap = new ConcurrentHashMap<>();
	// type/tripId?value , {sessionId, uuid, nickname}
	private final SimpMessagingTemplate template;
	private final String TOPIC_PREFIX = "/topic/api/trip/";

	public int getLastPlanJOrder(long tripId) {
		Integer order = planJRepository.findMaxOrder(tripId);
		if (order == null)
			return 0;
		return order + 1;
	}

	public String[] getEditorByDestination(char type, long tripId, int value) {
		return destinationMap.getOrDefault(type + "/" + tripId + "?" + value, null);
	}

	public void addEditor(char type, long tripId, int value, String sessionId, String uuid, String nickname) {
		String dest = type + "/" + tripId + "?" + value;
		destinationMap.put(dest, new String[] {sessionId, uuid, nickname});
		sessionIdMap.put(sessionId, dest);
	}

	public void checkIsEditor(char type, long tripId, int value, String uuid) {
		String dest = type + "/" + tripId + "?" + value;
		String[] editor = destinationMap.getOrDefault(dest, null);
		if (editor == null || !uuid.equals(editor[1])) {
			log.info("user is not editor of trip :" + tripId + "value : " + value + "\nuuid : " + uuid);
			throw new PermissionDeniedException("user is not editor of day");
		}
	}

	public void checkHasEditor(char type, long tripId, Integer value, String uuid) {
		String dest = type + "/" + tripId + "?" + value;
		String[] editor = destinationMap.getOrDefault(dest, null);
		if(editor != null && !editor[1].equals(uuid)) {
			log.info("there are editor at trip :" + tripId + "day : " + value + "\nuuid : " + uuid);
			throw new PermissionDeniedException("there are editor at destination already");
		}
	}

	public void removeEditorBySessionId(String sessionId) {
		String dest = sessionIdMap.getOrDefault(sessionId, null);
		if (dest == null)
			return;
		destinationMap.remove(dest);
		sessionIdMap.remove(sessionId);
		String[] dests = dest.split("\\?");
		int value = Integer.parseInt(dests[1]);
		template.convertAndSend(TOPIC_PREFIX + dests[0], new SocketResponseBody<>("edit finish", value));
	}

	public void removeEditorByDestination(char type, long tripId, int value, String uuid) {
		String dest = type + "/" + tripId + "?" + value;
		String[] editor = destinationMap.getOrDefault(dest, null);
		if (editor == null || !editor[1].equals(uuid))
			return;
		String sessionId = editor[0];
		destinationMap.remove(dest);
		sessionIdMap.remove(sessionId);

		template.convertAndSend(TOPIC_PREFIX + type + "/" + tripId,
			new SocketResponseBody<>("edit finish", new PlanJEditorRegisterDto(value, uuid, editor[2]))
		);
	}
}
