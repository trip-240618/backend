package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.planJ.entity.PlanJ;
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
public class PlanJEditService {
	private final PlanJRepository planJRepository;
	@Getter
	private final ConcurrentHashMap<String, String> sessionIdMap = new ConcurrentHashMap<>();
	// SessionId, "tripId/day"
	@Getter
	private final ConcurrentHashMap<String, String[]> destinationMap = new ConcurrentHashMap<>();
	// "tripId/day", {SessionId, uuid, nickname}
	private final SimpMessagingTemplate template;
	private final String TOPIC_PREFIX = "/topic/api/trip/j/";

	public int getLastOrderByTripId(long tripId) {
		Integer order = planJRepository.findMaxOrder(tripId);
		if (order == null)
			return 0;
		return order + 1;
	}

	public String[] getEditorByTripIdAndDay(long tripId, int day) {
		return destinationMap.getOrDefault(tripId + "/" + day, null);
	}

	public void addEditor(long tripId, int day, String sessionId, String uuid, String name) {
		String dest = tripId + "/" + day;
		destinationMap.put(dest, new String[] {sessionId, uuid, name});
		sessionIdMap.put(sessionId, dest);
	}

	public void checkIsEditor(long tripId, int day, String uuid) {
		String[] editor = destinationMap.getOrDefault(tripId + "/" + day, null);
		if (editor == null || !uuid.equals(editor[1])) {
			log.info("user is not editor of trip :" + tripId + "day : " + day + "\nuuid : " + uuid);
			throw new PermissionDeniedException("user is not editor of day");
		}
	}

	public void checkHasEditor(long tripId, int day, String uuid) {
		String[] editor = destinationMap.getOrDefault(tripId + "/" + day, null);
		if(editor != null && !editor[1].equals(uuid)) {
			log.info("there are editor at trip :" + tripId + "day : " + day + "\nuuid : " + uuid);
			throw new PermissionDeniedException("there are editor at destination already");
		}
	}

	@Transactional
	public int swapPlanJByIds(long planId1, long planId2) {
		PlanJ plan1 = planJRepository.findById(planId1).orElseThrow(NullPointerException::new);
		PlanJ plan2 = planJRepository.findById(planId2).orElseThrow(NullPointerException::new);

		LocalTime startTime1 = plan1.getStartTime();
		LocalTime startTime2 = plan2.getStartTime();
		int order1 = plan1.getOrderByDate();
		int order2 = plan2.getOrderByDate();

		return planJRepository.updateStartTimeAndOrder(planId1, startTime2, order2) +
			   planJRepository.updateStartTimeAndOrder(planId2, startTime1, order1);
	}

	public void removeEditorBySessionId(String sessionId) {
		String dest = sessionIdMap.getOrDefault(sessionId, null);
		if (dest == null)
			return;
		destinationMap.remove(dest);
		sessionIdMap.remove(sessionId);
		String[] dests = dest.split("/");
		long tripId = Long.parseLong(dests[0]);
		int day = Integer.parseInt(dests[1]);

		template.convertAndSend(TOPIC_PREFIX + tripId, new SocketResponseBody<>("edit finish", day));
	}

	public void removeEditorByDestination(long tripId, int day, String uuid) {
		String dest = tripId + "/" + day;
		String[] editor = destinationMap.getOrDefault(dest, null);
		if (editor == null || !editor[1].equals(uuid))
			return;
		String sessionId = editor[0];
		destinationMap.remove(dest);
		sessionIdMap.remove(sessionId);
	}
}
