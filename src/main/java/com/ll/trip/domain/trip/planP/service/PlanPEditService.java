package com.ll.trip.domain.trip.planP.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planP.dto.PlanPEditRegisterDto;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;
import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.global.handler.exception.PermissionDeniedException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PlanPEditService {

	private final PlanPRepository planPRepository;

	private final String TOPIC_PREFIX = "/topic/api/trip/p/";
	@Getter
	private final ConcurrentHashMap<String, Long> sessionIdMap = new ConcurrentHashMap<>();
	// sessionId, tripId
	@Getter
	private final ConcurrentHashMap<Long, String[]> destinationMap = new ConcurrentHashMap<>();
	// tripId, {sessionId, uuid, nickname}
	private final SimpMessagingTemplate template;

	public void removeEditorBySessionId(String sessionId) {
		Long tripId = sessionIdMap.computeIfPresent(sessionId, (id, value) -> sessionIdMap.remove(id));
		if (tripId == null)
			return;
		destinationMap.computeIfPresent(tripId, (id, value) -> {
			template.convertAndSend(TOPIC_PREFIX + tripId, new SocketResponseBody<>("edit finish", value[2]));
			return destinationMap.remove(id);
		});
	}

	public void addEditor(long tripId, String sessionId, String uuid, String nickname) {
		destinationMap.put(tripId, new String[] {sessionId, uuid, nickname});
		sessionIdMap.put(sessionId, tripId);
	}

	public String[] getEditorByTripId(long tripId) {
		return destinationMap.getOrDefault(tripId, null);
	}

	public void checkIsEditor(long tripId, String uuid) {
		if (!uuid.equals(destinationMap.getOrDefault(tripId, new String[3])[1])) {
			log.info("user:" + uuid + "\nisn't editor of trip: " + tripId);
			throw new PermissionDeniedException("user isn't editor of trip");
		}
	}

	public void removeEditorByDestination(long tripId, String uuid) {
		checkIsEditor(tripId, uuid);
		String[] editor = destinationMap.remove(tripId);
		sessionIdMap.remove(editor[0], tripId);
		template.convertAndSend(TOPIC_PREFIX + tripId,
			new SocketResponseBody<>("edit finish", new PlanPEditRegisterDto(editor[1], editor[2])));
	}
}
