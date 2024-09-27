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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanJEditService {
	private final PlanJRepository planJRepository;
	@Getter
	private final ConcurrentHashMap<Long, ConcurrentHashMap<String, Integer>> activeEditTopicsAndUuidAndDay = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate template;
	private final String TOPIC_PREFIX = "/topic/api/trip/j/";

	public int getLastOrderByTripId(long tripId) {
		Integer order = planJRepository.findMaxOrder(tripId);
		if (order == null)
			return 0;
		return order + 1;
	}

	public String getEditorByTripIdAndDay(Long tripId, int day) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(tripId, null);

		if (map == null)
			return null;

		return map.entrySet()
			.stream()
			.filter(entry -> entry.getValue().equals(day))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}

	public void addEditor(long tripId, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(tripId)) {
			activeEditTopicsAndUuidAndDay.put(tripId, new ConcurrentHashMap<>());
		}

		activeEditTopicsAndUuidAndDay.get(tripId).put(uuid, day);
	}

	public boolean isEditor(long tripId, String uuid, int day) {
		if (!activeEditTopicsAndUuidAndDay.containsKey(tripId))
			return false;
		return activeEditTopicsAndUuidAndDay.get(tripId).get(uuid) == day;
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

	public void editorClosedSubscription(long tripId, String uuid) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(tripId, null);
		map.remove(uuid);
		template.convertAndSend(TOPIC_PREFIX + tripId, new PlanResponseBody<>("edit finish", uuid));
	}

	public void removeEditor(long tripId, int day, String uuid) {
		ConcurrentHashMap<String, Integer> map = activeEditTopicsAndUuidAndDay.getOrDefault(tripId, null);
		map.remove(uuid, day);
	}
}
