package com.ll.trip.domain.trip.planP.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.trip.websoket.response.SocketResponseBody;
import com.ll.trip.domain.trip.planP.repository.PlanPRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanPEditService {

	private final PlanPRepository planPRepository;

	private final String TOPIC_PREFIX = "/topic/api/trip/p/";
	@Getter
	private final ConcurrentHashMap<Long, String> activeEditTopicsAndUuid = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate template;

	public void editorClosedSubscription(long tripId, String username) {
		String uuid = activeEditTopicsAndUuid.getOrDefault(tripId, null);

		if (uuid != null && uuid.equals(username)) {
			activeEditTopicsAndUuid.remove(tripId);

			template.convertAndSend(TOPIC_PREFIX + tripId, new SocketResponseBody<>("edit finish", username));
		}
	}

	public void addEditor(long tripId, String username) {
		activeEditTopicsAndUuid.put(tripId, username);
	}

	public String getEditorByTripId(long tripId) {
		return activeEditTopicsAndUuid.getOrDefault(tripId, null);
	}

	@Transactional
	public int movePlanByDayAndOrder(long tripId, long planId, int dayFrom, int dayTo, int orderFrom, int orderTo) {
		if (dayFrom == dayTo) {
			return movePlanPInSameDay(tripId, planId, dayTo, orderFrom, orderTo);
		} else {
			return movePlanPInAnotherDay(tripId, planId, dayTo, orderTo);
		}
	}

	@Transactional
	public int movePlanPInSameDay(long tripId, long planId, int dayTo, int orderFrom, int orderTo) {
		int updated = 0;

		if (orderTo > orderFrom) {
			updated += planPRepository.reduceOrderFromToByTripIdAndDay(tripId, dayTo, orderFrom + 1,
				orderTo);
		} else {
			updated += planPRepository.increaseOrderFromToByTripIdAndDay(tripId, dayTo, orderTo,
				orderFrom - 1);

		}
		updated += planPRepository.updateDayOrderById(planId, dayTo, orderTo);

		return updated;
	}

	@Transactional
	public int movePlanPInAnotherDay(long tripId, long planId, int dayTo, int orderTo) {
		int updated = 0;

		updated += planPRepository.increaseOrderFromByTripIdAndDay(tripId, dayTo, orderTo);
		updated += planPRepository.updateDayOrderById(planId, dayTo, orderTo);

		return updated;
	}

	public boolean isEditor(long tripId, String uuid) {
		return activeEditTopicsAndUuid.get(tripId).equals(uuid);
	}

	public void removeEditor(long tripId, String uuid) {
		this.activeEditTopicsAndUuid.remove(tripId, uuid);
	}
}
