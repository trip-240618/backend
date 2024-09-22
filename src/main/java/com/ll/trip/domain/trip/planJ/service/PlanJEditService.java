package com.ll.trip.domain.trip.planJ.service;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.location.response.PlanResponseBody;
import com.ll.trip.domain.trip.planJ.entity.PlanJ;
import com.ll.trip.domain.trip.planJ.repository.PlanJRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJEditService {
	private final PlanJRepository planJRepository;
	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> activeEditTopicsAndUuidAndDay = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate template;
	private final String TOPIC_PREFIX = "/topic/api/trip/j/";

	public int getLastOrderByTripId(long tripId, int dayAfterStart) {
		Integer order = planJRepository.findMaxOrder(tripId, dayAfterStart);
		if (order == null)
			return 0;
		return order + 1;
	}

	public String getEditorByInvitationCodeAndDay(String invitationCode, int day) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(invitationCode, null);

		if (map == null)
			return null;

		return map.entrySet()
			.stream()
			.filter(entry -> entry.getValue().equals(day))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}

	public void addEditor(String invitationCode, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(invitationCode)) {
			activeEditTopicsAndUuidAndDay.put(invitationCode, new ConcurrentHashMap<>());
		}

		activeEditTopicsAndUuidAndDay.get(invitationCode).put(uuid, day);
	}

	public boolean isEditor(String invitationCode, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(invitationCode))
			return false;
		return activeEditTopicsAndUuidAndDay.get(invitationCode).get(uuid) == day;
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

	public void editorClosedSubscription(String invitationCode, String uuid) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(invitationCode, null);
		map.remove(uuid);
		template.convertAndSend(TOPIC_PREFIX + invitationCode, new PlanResponseBody<>("edit finish", uuid));
	}

	public void removeEditor(String invitationCode, int day, String uuid) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(invitationCode, null);
		map.remove(uuid, day);
	}
}
